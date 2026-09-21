package com.smartwake.app.ui.crear

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smartwake.app.R
import com.smartwake.app.data.Place
import com.smartwake.app.data.Places
import com.smartwake.app.ui.components.BlockButton
import com.smartwake.app.ui.components.BottomActionBar
import com.smartwake.app.ui.components.IconContainer
import com.smartwake.app.ui.components.InsetDivider
import com.smartwake.app.ui.components.KitTopAppBar
import com.smartwake.app.ui.components.outlineBorder
import com.smartwake.app.ui.theme.KitTypography
import com.smartwake.app.ui.theme.SmartWakeTheme
import com.smartwake.app.ui.theme.SmartWakeType
import com.smartwake.app.ui.theme.Tangerine
import java.text.Normalizer

/**
 * Figma: "Vista Ubicaciones (Dark)" (267:929), used for both Origen and Destino.
 * The map is a placeholder, as in the design; the search box filters the saved places.
 */
@Composable
fun UbicacionesScreen(
    forOrigin: Boolean,
    current: Place?,
    onBack: () -> Unit,
    onConfirm: (Place) -> Unit,
) {
    val places = if (forOrigin) listOf(Places.CurrentLocation) + Places.favorites else Places.favorites
    var query by rememberSaveable { mutableStateOf("") }
    var selectedId by rememberSaveable { mutableStateOf(current?.id) }
    val matches = places.filter { it.matches(query) }
    val selected = places.firstOrNull { it.id == selectedId }

    Scaffold(
        topBar = {
            KitTopAppBar(
                title = stringResource(R.string.ubicaciones_titulo),
                containerColor = MaterialTheme.colorScheme.surface,
                onBack = onBack,
            )
        },
        bottomBar = {
            BottomActionBar(color = MaterialTheme.colorScheme.surface) {
                BlockButton(
                    text = stringResource(R.string.ubicaciones_confirmar),
                    onClick = { selected?.let(onConfirm) },
                    enabled = selected != null,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            SearchField(
                query = query,
                onQueryChange = { query = it },
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            )
            MapPlaceholder(Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp))
            Column(
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = stringResource(R.string.ubicaciones_favoritas),
                    style = SmartWakeType.rowTitle,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                if (matches.isEmpty()) {
                    Text(
                        text = stringResource(R.string.ubicaciones_sin_resultados),
                        style = SmartWakeType.rowBody,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                } else {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = outlineBorder(),
                    ) {
                        Column(Modifier.selectableGroup()) {
                            matches.forEachIndexed { index, place ->
                                if (index > 0) InsetDivider()
                                PlaceRow(
                                    place = place,
                                    selected = place.id == selectedId,
                                    onClick = { selectedId = place.id },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Case- and accent-insensitive match on name or address. */
private fun Place.matches(query: String): Boolean {
    if (query.isBlank()) return true
    val needle = query.trim().normalized()
    return name.normalized().contains(needle) || address.normalized().contains(needle)
}

private fun String.normalized(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD).replace(Regex("\\p{M}+"), "").lowercase()

/** Figma "Search bar" from the Material 3 kit. */
@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val textStyle = KitTypography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                Icon(painterResource(R.drawable.ic_search), contentDescription = null, tint = Tangerine)
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp, end = 20.dp),
                singleLine = true,
                textStyle = textStyle,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                decorationBox = { field ->
                    if (query.isEmpty()) {
                        Text(stringResource(R.string.ubicaciones_buscar), style = textStyle)
                    }
                    field()
                },
            )
        }
    }
}

@Composable
private fun MapPlaceholder(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = outlineBorder(),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(painterResource(R.drawable.ic_location_on), contentDescription = null, tint = Tangerine)
        }
    }
}

@Composable
private fun PlaceRow(place: Place, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            // Figma has no selected state; highlight the row so the choice is visible.
            .background(if (selected) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconContainer(icon = R.drawable.ic_location_on, tint = Tangerine)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(place.name, style = SmartWakeType.rowTitle, color = MaterialTheme.colorScheme.onSurface)
            if (place.address.isNotEmpty()) {
                Text(place.address, style = SmartWakeType.rowBody, color = MaterialTheme.colorScheme.onSurface)
            }
        }
        Icon(
            painter = painterResource(if (selected) R.drawable.ic_check else R.drawable.ic_arrow_right),
            contentDescription = null,
            tint = Tangerine,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Preview(widthDp = 400, heightDp = 900)
@Composable
private fun UbicacionesPreview() {
    SmartWakeTheme {
        UbicacionesScreen(forOrigin = false, current = null, onBack = {}, onConfirm = {})
    }
}
