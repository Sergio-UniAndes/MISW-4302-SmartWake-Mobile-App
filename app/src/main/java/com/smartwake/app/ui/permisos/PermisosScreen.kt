package com.smartwake.app.ui.permisos

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smartwake.app.R
import com.smartwake.app.ui.components.IconContainer
import com.smartwake.app.ui.components.SmartWakeLogo
import com.smartwake.app.ui.components.SmartWakeSwitch
import com.smartwake.app.ui.components.outlineBorder
import com.smartwake.app.ui.theme.SmartWakeTheme

private val CardShape = RoundedCornerShape(12.dp)

/** Figma: Mobile MockUp > "Vista Permisos" (node 267:639). */
@Composable
fun PermisosScreen(onEmpezar: () -> Unit, modifier: Modifier = Modifier) {
    // Visual only for now: toggling doesn't request the OS permission yet.
    var ubicacionActiva by rememberSaveable { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(start = 24.dp, top = 30.dp, end = 24.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Scrolls on short screens; on tall ones the free space sits above the CTA like the Figma spacer.
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SmartWakeLogo()
            Intro()
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PermissionCard(
                    icon = R.drawable.ic_location_on,
                    title = stringResource(R.string.permiso_ubicacion_titulo),
                    description = stringResource(R.string.permiso_ubicacion_descripcion),
                    checked = ubicacionActiva,
                    onCheckedChange = { ubicacionActiva = it },
                )
                PermissionCard(
                    icon = R.drawable.ic_alarm,
                    title = stringResource(R.string.permiso_alarmas_titulo),
                    description = stringResource(R.string.permiso_alarmas_descripcion),
                )
            }
        }
        Button(
            onClick = onEmpezar,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            // The Figma button renders with square corners (its outer frame is filled edge to edge).
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text(
                text = stringResource(R.string.permisos_empezar),
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
private fun Intro() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.permisos_titulo),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = stringResource(R.string.permisos_intro),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

/** A permission row. Pass [checked] to show a switch; the whole card toggles it. */
@Composable
private fun PermissionCard(
    @DrawableRes icon: Int,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    checked: Boolean? = null,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        color = MaterialTheme.colorScheme.surface,
        border = outlineBorder(),
    ) {
        Row(
            modifier = Modifier
                .then(
                    if (checked != null) {
                        Modifier.toggleable(
                            value = checked,
                            role = Role.Switch,
                            onValueChange = onCheckedChange,
                        )
                    } else {
                        Modifier
                    }
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconContainer(
                icon = icon,
                iconSize = 24.dp,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                shape = CardShape,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            if (checked != null) {
                // Handled by the card's toggleable so the whole row is one touch target.
                SmartWakeSwitch(checked = checked, onCheckedChange = null)
            }
        }
    }
}

@Preview(widthDp = 400, heightDp = 900)
@Composable
private fun PermisosScreenPreview() {
    SmartWakeTheme {
        PermisosScreen(onEmpezar = {})
    }
}
