package com.smartwake.app.ui.crear

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smartwake.app.R
import com.smartwake.app.data.Alarm
import com.smartwake.app.data.Transport
import com.smartwake.app.ui.components.SmartWakeSwitch
import com.smartwake.app.ui.components.TopBarContainer
import com.smartwake.app.ui.components.outlineBorder
import com.smartwake.app.ui.formatPadded
import com.smartwake.app.ui.fullName
import com.smartwake.app.ui.period
import com.smartwake.app.ui.theme.Amber
import com.smartwake.app.ui.theme.KitTheme
import com.smartwake.app.ui.theme.KitTypography
import com.smartwake.app.ui.theme.SmartWakeTheme
import com.smartwake.app.ui.theme.SmartWakeType
import java.time.DayOfWeek

private const val MaxNameLength = 40
private val SectionShape = RoundedCornerShape(16.dp)
private val ChipShape = RoundedCornerShape(8.dp)

/** The form's day chips use X for Wednesday, as in Figma. */
private val FormDayLetters = listOf("L", "M", "X", "J", "V", "S", "D")

private data class TransportOption(val transport: Transport, @DrawableRes val icon: Int, val label: Int)

private val TransportOptions = listOf(
    TransportOption(Transport.BUS, R.drawable.ic_directions_bus, R.string.transporte_bus),
    TransportOption(Transport.CAR, R.drawable.ic_directions_car, R.string.transporte_coche),
    TransportOption(Transport.BIKE, R.drawable.ic_bike, R.string.transporte_bici),
    TransportOption(Transport.WALK, R.drawable.ic_directions_walk, R.string.transporte_a_pie),
)

/** Figma: "Vista Crear Nueva Alarma (Dark)" (267:690) plus the "Hora de Llegada" dialog (267:966). */
@Composable
fun CrearAlarmaScreen(
    draft: Alarm,
    onDraftChange: ((Alarm) -> Alarm) -> Unit,
    onClose: () -> Unit,
    onSave: () -> Unit,
    onPickOrigin: () -> Unit,
    onPickDestination: () -> Unit,
    onEditPrep: () -> Unit,
) {
    var showArrivalDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = { CrearTopBar(canSave = draft.isComplete, onClose = onClose, onSave = onSave) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            NameField(
                name = draft.name,
                onNameChange = { name -> onDraftChange { it.copy(name = name.take(MaxNameLength)) } },
            )
            Section {
                SectionHeader(R.drawable.ic_location_on, stringResource(R.string.crear_ubicaciones), iconSize = 24)
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    PlaceField(
                        label = stringResource(R.string.crear_origen),
                        value = draft.origin.name,
                        onClick = onPickOrigin,
                    )
                    PlaceField(
                        label = stringResource(R.string.crear_destino),
                        value = draft.destination?.name ?: stringResource(R.string.crear_destino_vacio),
                        isPlaceholder = draft.destination == null,
                        onClick = onPickDestination,
                    )
                }
            }
            Section(onClick = { showArrivalDialog = true }) {
                SectionHeader(R.drawable.ic_clock, stringResource(R.string.crear_llegada), small = true)
                ValueWithUnit(draft.arrival.formatPadded(), draft.arrival.period)
            }
            Section(onClick = onEditPrep) {
                SectionHeader(R.drawable.ic_timer, stringResource(R.string.crear_prep), small = true)
                ValueWithUnit(draft.prepMinutes.toString(), stringResource(R.string.minutos_unidad))
            }
            Section {
                SectionHeader(R.drawable.ic_arrow_forward, stringResource(R.string.crear_transporte))
                Row(
                    modifier = Modifier.selectableGroup(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TransportOptions.forEach { option ->
                        TransportChip(
                            option = option,
                            selected = draft.transport == option.transport,
                            onClick = { onDraftChange { it.copy(transport = option.transport) } },
                        )
                    }
                }
            }
            Section {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .toggleable(
                            value = draft.repeat,
                            role = Role.Switch,
                            onValueChange = { repeat -> onDraftChange { it.copy(repeat = repeat) } },
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SectionHeader(
                        R.drawable.ic_repeat,
                        stringResource(R.string.crear_repetir),
                        modifier = Modifier.weight(1f),
                    )
                    // Handled by the row's toggleable so the label is part of the touch target.
                    SmartWakeSwitch(checked = draft.repeat, onCheckedChange = null)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DayOfWeek.entries.forEachIndexed { index, day ->
                        DayChip(
                            letter = FormDayLetters[index],
                            day = day,
                            selected = day in draft.repeatDays,
                            enabled = draft.repeat,
                            onToggle = { on ->
                                onDraftChange {
                                    it.copy(repeatDays = if (on) it.repeatDays + day else it.repeatDays - day)
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    if (showArrivalDialog) {
        HoraLlegadaDialog(
            initial = draft.arrival,
            onConfirm = { time ->
                onDraftChange { it.copy(arrival = time) }
                showArrivalDialog = false
            },
            onDismiss = { showArrivalDialog = false },
        )
    }
}

@Composable
private fun CrearTopBar(canSave: Boolean, onClose: () -> Unit, onSave: () -> Unit) {
    TopBarContainer(color = MaterialTheme.colorScheme.surfaceVariant) {
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 4.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = stringResource(R.string.crear_cerrar),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        Text(
            text = stringResource(R.string.crear_titulo),
            style = SmartWakeType.barTitle,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.Center),
        )
        Button(
            onClick = onSave,
            enabled = canSave,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            Text(stringResource(R.string.crear_guardar), style = KitTypography.labelLarge)
        }
    }
}

@Composable
private fun NameField(name: String, onNameChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.crear_nombre),
            style = SmartWakeType.fieldLabel,
            color = MaterialTheme.colorScheme.onBackground,
        )
        // The Figma text field is the Material 3 kit component (Roboto, notched label).
        KitTheme {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.crear_nombre)) },
                singleLine = true,
                shape = RoundedCornerShape(4.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done,
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MaterialTheme.colorScheme.primary,
                ),
            )
        }
    }
}

/** A form card; clickable when [onClick] is set. */
@Composable
private fun Section(onClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    val body: @Composable () -> Unit = {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content,
        )
    }
    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = SectionShape,
            color = MaterialTheme.colorScheme.surface,
            border = outlineBorder(),
            content = body,
        )
    } else {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = SectionShape,
            color = MaterialTheme.colorScheme.surface,
            border = outlineBorder(),
            content = body,
        )
    }
}

/** Icon + title. [small] headers (Llegada, Prep.) use the 12sp label style. */
@Composable
private fun SectionHeader(
    @DrawableRes icon: Int,
    title: String,
    modifier: Modifier = Modifier,
    iconSize: Int = 18,
    small: Boolean = false,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(iconSize.dp),
        )
        Text(
            text = title,
            style = if (small) SmartWakeType.fieldLabel else SmartWakeType.cardHeader,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun PlaceField(label: String, value: String, onClick: () -> Unit, isPlaceholder: Boolean = false) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(role = Role.Button, onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(label, style = SmartWakeType.fieldLabel, color = MaterialTheme.colorScheme.onSurface)
        Text(
            text = value,
            style = SmartWakeType.fieldValue,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = if (isPlaceholder) Modifier.alpha(0.6f) else Modifier,
        )
    }
}

@Composable
private fun ValueWithUnit(value: String, unit: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = value,
            style = SmartWakeType.bigValue,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.alignByBaseline(),
        )
        Text(
            text = unit,
            style = SmartWakeType.unit,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.alignByBaseline(),
        )
    }
}

@Composable
private fun TransportChip(option: TransportOption, selected: Boolean, onClick: () -> Unit) {
    val label = stringResource(option.label)
    Box(
        modifier = Modifier
            .height(32.dp)
            .clip(ChipShape)
            .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
            .border(1.dp, if (selected) Amber else MaterialTheme.colorScheme.outline, ChipShape)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .semantics { contentDescription = label }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(option.icon),
            contentDescription = null,
            tint = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun DayChip(letter: String, day: DayOfWeek, selected: Boolean, enabled: Boolean, onToggle: (Boolean) -> Unit) {
    val dayName = day.fullName()
    Box(
        modifier = Modifier
            .size(32.dp)
            .alpha(if (enabled) 1f else 0.38f)
            .clip(ChipShape)
            .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
            .border(1.dp, if (selected) Amber else MaterialTheme.colorScheme.outline, ChipShape)
            .toggleable(value = selected, enabled = enabled, role = Role.Checkbox, onValueChange = onToggle)
            .semantics { contentDescription = dayName },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = letter,
            style = KitTypography.labelLarge,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview(widthDp = 400, heightDp = 900)
@Composable
private fun CrearAlarmaPreview() {
    SmartWakeTheme {
        CrearAlarmaScreen(
            draft = Alarm(name = "Reunión matutina"),
            onDraftChange = {},
            onClose = {},
            onSave = {},
            onPickOrigin = {},
            onPickDestination = {},
            onEditPrep = {},
        )
    }
}
