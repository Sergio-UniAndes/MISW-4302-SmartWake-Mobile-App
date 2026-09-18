package com.smartwake.app.ui.crear

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smartwake.app.R
import com.smartwake.app.data.Alarm
import com.smartwake.app.data.Places
import com.smartwake.app.data.Transport
import com.smartwake.app.data.nextRingDate
import com.smartwake.app.data.schedule
import com.smartwake.app.ui.components.BlockButton
import com.smartwake.app.ui.components.BottomActionBar
import com.smartwake.app.ui.components.IconContainer
import com.smartwake.app.ui.components.InsetDivider
import com.smartwake.app.ui.components.KitTopAppBar
import com.smartwake.app.ui.components.SmartWakeSwitch
import com.smartwake.app.ui.components.outlineBorder
import com.smartwake.app.ui.formatPaddedWithPeriod
import com.smartwake.app.ui.formatSummaryDate
import com.smartwake.app.ui.theme.SmartWakeTheme
import com.smartwake.app.ui.theme.SmartWakeType
import java.time.LocalDateTime

/** Figma: "Vista Resumen Alarma (Dark)" (267:761). */
@Composable
fun ResumenAlarmaScreen(
    draft: Alarm,
    onBack: () -> Unit,
    onDepartureReminderChange: (Boolean) -> Unit,
    onConfirm: () -> Unit,
) {
    val schedule = draft.schedule()
    val date = remember(draft) { draft.nextRingDate(LocalDateTime.now()) }

    Scaffold(
        topBar = {
            KitTopAppBar(
                title = stringResource(R.string.resumen_barra),
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                onBack = onBack,
            )
        },
        bottomBar = {
            BottomActionBar(color = MaterialTheme.colorScheme.surfaceVariant) {
                BlockButton(text = stringResource(R.string.accion_confirmar), onClick = onConfirm)
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(R.string.resumen_titulo),
                    style = SmartWakeType.summaryTitle,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.semantics { heading() },
                )
                Text(
                    text = date.formatSummaryDate(),
                    style = SmartWakeType.summaryDate,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            // The card stretches to the bottom action like in Figma; its content scrolls on short screens.
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = outlineBorder(),
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                ) {
                    TimelineItem(
                        icon = R.drawable.ic_alarm,
                        value = schedule.wake.formatPaddedWithPeriod(),
                        label = stringResource(R.string.resumen_despertar),
                    )
                    InsetDivider()
                    TimelineItem(
                        icon = R.drawable.ic_timer,
                        value = stringResource(R.string.minutos, schedule.prepMinutes),
                        label = stringResource(R.string.resumen_preparacion),
                    )
                    InsetDivider()
                    DepartureItem(
                        value = schedule.departure.formatPaddedWithPeriod(),
                        reminder = draft.departureReminder,
                        onReminderChange = onDepartureReminderChange,
                    )
                    InsetDivider()
                    TimelineItem(
                        icon = draft.transport.icon(),
                        value = stringResource(R.string.minutos, schedule.totalTravelMinutes),
                        label = stringResource(R.string.resumen_viaje),
                    )
                    InsetDivider()
                    TimelineItem(
                        icon = R.drawable.ic_location_on,
                        value = schedule.arrival.formatPaddedWithPeriod(),
                        label = stringResource(R.string.resumen_llegada),
                        showConnector = false,
                    )
                }
            }
        }
    }
}

@DrawableRes
private fun Transport.icon() = when (this) {
    Transport.BUS -> R.drawable.ic_directions_bus
    Transport.CAR -> R.drawable.ic_directions_car
    Transport.BIKE -> R.drawable.ic_bike
    Transport.WALK -> R.drawable.ic_directions_walk
}

/** Icon circle with the connector line below it, as drawn in the Figma timeline. */
@Composable
private fun TimelineIcon(@DrawableRes icon: Int, containerColor: Color, showConnector: Boolean) {
    Column(modifier = Modifier.width(40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        IconContainer(icon = icon, containerColor = containerColor, bordered = false)
        if (showConnector) {
            Box(
                Modifier
                    .width(2.dp)
                    .height(24.dp)
                    .background(MaterialTheme.colorScheme.outline),
            )
        }
    }
}

@Composable
private fun TimelineItem(
    @DrawableRes icon: Int,
    value: String,
    label: String,
    showConnector: Boolean = true,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TimelineIcon(icon, MaterialTheme.colorScheme.surfaceVariant, showConnector)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(value, style = SmartWakeType.timelineValue, color = MaterialTheme.colorScheme.onSurface)
            Text(label, style = SmartWakeType.timelineLabel, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun DepartureItem(value: String, reminder: Boolean, onReminderChange: (Boolean) -> Unit) {
    val note = stringResource(R.string.resumen_salida_nota)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TimelineIcon(R.drawable.ic_notifications, MaterialTheme.colorScheme.surface, showConnector = true)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(value, style = SmartWakeType.timelineValue, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    text = stringResource(R.string.resumen_salida),
                    style = SmartWakeType.timelineLabel,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Text(note, style = SmartWakeType.timelineNote, color = MaterialTheme.colorScheme.onSurface)
            SmartWakeSwitch(
                checked = reminder,
                onCheckedChange = onReminderChange,
                modifier = Modifier
                    .align(Alignment.End)
                    .semantics { contentDescription = note },
            )
        }
    }
}

@Preview(widthDp = 400, heightDp = 900)
@Composable
private fun ResumenAlarmaPreview() {
    SmartWakeTheme {
        ResumenAlarmaScreen(
            draft = Alarm(name = "Reunión matutina", destination = Places.favorites.first()),
            onBack = {},
            onDepartureReminderChange = {},
            onConfirm = {},
        )
    }
}
