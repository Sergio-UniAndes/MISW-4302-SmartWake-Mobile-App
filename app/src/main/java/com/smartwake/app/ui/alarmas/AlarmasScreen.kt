package com.smartwake.app.ui.alarmas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smartwake.app.R
import com.smartwake.app.data.Alarm
import com.smartwake.app.data.Places
import com.smartwake.app.data.schedule
import com.smartwake.app.ui.components.SmartWakeLogo
import com.smartwake.app.ui.components.SmartWakeSwitch
import com.smartwake.app.ui.components.TopBarContainer
import com.smartwake.app.ui.components.outlineBorder
import com.smartwake.app.ui.formatShort
import com.smartwake.app.ui.formatShortWithPeriod
import com.smartwake.app.ui.fullName
import com.smartwake.app.ui.period
import com.smartwake.app.ui.theme.Cream
import com.smartwake.app.ui.theme.KitTheme
import com.smartwake.app.ui.theme.SmartWakeTheme
import com.smartwake.app.ui.theme.SmartWakeType
import com.smartwake.app.ui.theme.Tangerine
import java.time.DayOfWeek

/** The list's day circles use M for both Tuesday and Wednesday, as in Figma. */
private val ListDayLetters = listOf("L", "M", "M", "J", "V", "S", "D")

/**
 * Figma: "Vista Lista Vacia (Dark)" (267:668) when there are no alarms,
 * "Vista Lista Alarmas (Dark)" (267:820) otherwise, and "Vista Eliminar Alarma - Dialog (Dark)" (267:923).
 */
@Composable
fun AlarmasScreen(
    alarms: List<Alarm>,
    onAddAlarm: () -> Unit,
    onEnabledChange: (id: Long, enabled: Boolean) -> Unit,
    onDelete: (id: Long) -> Unit,
) {
    var pendingDeleteId by rememberSaveable { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            HomeTopBar(
                color = if (alarms.isEmpty()) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.surface
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddAlarm,
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = stringResource(R.string.lista_nueva_alarma),
                    modifier = Modifier.size(28.dp),
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        if (alarms.isEmpty()) {
            EmptyState(Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                // Bottom room so the FAB never covers the last card.
                contentPadding = PaddingValues(start = 24.dp, top = 16.dp, end = 24.dp, bottom = 112.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(alarms, key = { it.id }) { alarm ->
                    AlarmCard(
                        alarm = alarm,
                        onEnabledChange = { onEnabledChange(alarm.id, it) },
                        onDeleteClick = { pendingDeleteId = alarm.id },
                    )
                }
            }
        }
    }

    pendingDeleteId?.let { id ->
        DeleteAlarmDialog(
            onConfirm = {
                onDelete(id)
                pendingDeleteId = null
            },
            onDismiss = { pendingDeleteId = null },
        )
    }
}

@Composable
private fun HomeTopBar(color: Color) {
    TopBarContainer(color = color) {
        Text(
            text = stringResource(R.string.lista_titulo),
            style = SmartWakeType.barTitle,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .align(Alignment.Center)
                .semantics { heading() },
        )
        Surface(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = outlineBorder(),
        ) {
            Text(
                text = stringResource(R.string.lista_id_dispositivo),
                style = SmartWakeType.badge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            )
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, top = 24.dp, end = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SmartWakeLogo()
        Spacer(Modifier.height(36.dp))
        Text(
            text = stringResource(R.string.lista_vacia_titulo),
            style = SmartWakeType.emptyTitle,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.lista_vacia_texto),
            style = SmartWakeType.emptyBody,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun AlarmCard(
    alarm: Alarm,
    onEnabledChange: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
) {
    val schedule = alarm.schedule()
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = outlineBorder(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_location_on),
                    contentDescription = null,
                    tint = Tangerine,
                    modifier = Modifier.size(24.dp),
                )
                Text(
                    text = alarm.destination?.name.orEmpty().uppercase(),
                    style = SmartWakeType.placeTag,
                    color = Tangerine,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                )
                FilledIconButton(
                    onClick = onDeleteClick,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Cream,
                    ),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = stringResource(R.string.alarma_eliminar),
                    )
                }
                val switchLabel = stringResource(R.string.alarma_activa)
                SmartWakeSwitch(
                    checked = alarm.enabled,
                    onCheckedChange = onEnabledChange,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .semantics { contentDescription = switchLabel },
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "~" + schedule.wake.formatShort(),
                    style = SmartWakeType.wakeTime,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.alignByBaseline(),
                )
                Text(
                    text = schedule.wake.period,
                    style = SmartWakeType.wakePeriod,
                    color = Tangerine,
                    modifier = Modifier.alignByBaseline(),
                )
            }
            Text(
                text = stringResource(R.string.alarma_llegada, alarm.arrival.formatShortWithPeriod()),
                style = SmartWakeType.arrival,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (schedule.trafficMinutes > 0) {
                TrafficNote(schedule.trafficMinutes)
            }
            RepeatDays(alarm)
        }
    }
}

@Composable
private fun TrafficNote(minutes: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = outlineBorder(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("⚠", style = SmartWakeType.warningGlyph, color = Tangerine)
            Text(
                text = stringResource(R.string.alarma_ajuste_trafico, minutes),
                style = SmartWakeType.warning,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun RepeatDays(alarm: Alarm) {
    val activeDays = if (alarm.repeat) alarm.repeatDays else emptySet()
    val description = if (activeDays.isEmpty()) {
        stringResource(R.string.alarma_no_repite)
    } else {
        stringResource(
            R.string.alarma_repite,
            DayOfWeek.entries.filter { it in activeDays }.joinToString { it.fullName() },
        )
    }
    Row(
        modifier = Modifier.clearAndSetSemantics { contentDescription = description },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DayOfWeek.entries.forEachIndexed { index, day ->
            val active = day in activeDays
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .then(
                        if (active) {
                            Modifier.background(MaterialTheme.colorScheme.primary, CircleShape)
                        } else {
                            Modifier
                                .background(MaterialTheme.colorScheme.surface, CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        }
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = ListDayLetters[index],
                    style = SmartWakeType.dayChip,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun DeleteAlarmDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    KitTheme {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                // Figma uses the location pin here.
                Icon(painterResource(R.drawable.ic_location_on), contentDescription = null)
            },
            title = { Text(stringResource(R.string.eliminar_titulo)) },
            text = { Text(stringResource(R.string.eliminar_texto)) },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Cream,
                    ),
                ) {
                    Text(stringResource(R.string.eliminar_confirmar))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(contentColor = Cream),
                ) {
                    Text(stringResource(R.string.accion_cancelar))
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            iconContentColor = Tangerine,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview(widthDp = 400, heightDp = 900)
@Composable
private fun AlarmasVaciaPreview() {
    SmartWakeTheme {
        AlarmasScreen(alarms = emptyList(), onAddAlarm = {}, onEnabledChange = { _, _ -> }, onDelete = {})
    }
}

@Preview(widthDp = 400, heightDp = 900)
@Composable
private fun AlarmasListaPreview() {
    SmartWakeTheme {
        AlarmasScreen(
            alarms = listOf(Alarm(id = 1, name = "Reunión matutina", destination = Places.favorites.first())),
            onAddAlarm = {},
            onEnabledChange = { _, _ -> },
            onDelete = {},
        )
    }
}
