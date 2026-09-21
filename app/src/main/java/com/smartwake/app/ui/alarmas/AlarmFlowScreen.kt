package com.smartwake.app.ui.alarmas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.activity.compose.BackHandler
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.smartwake.app.R
import com.smartwake.app.data.Alarm
import com.smartwake.app.data.schedule
import com.smartwake.app.ui.AlarmFlow
import com.smartwake.app.ui.formatPadded
import com.smartwake.app.ui.theme.Coral
import com.smartwake.app.ui.theme.Cream
import com.smartwake.app.ui.theme.Ember
import com.smartwake.app.ui.theme.Moss
import com.smartwake.app.ui.theme.MossLight
import com.smartwake.app.ui.theme.Bark
import com.smartwake.app.ui.theme.SmartWakeType

@Composable
fun AlarmFlowScreen(
    flow: AlarmFlow,
    alarm: Alarm,
    onAdvance: () -> Unit,
    onFlowChange: (AlarmFlow) -> Unit,
    onStop: () -> Unit,
    onDismiss: () -> Unit,
) {
    BackHandler(onBack = onDismiss)
    if (flow == AlarmFlow.VOICE_NOTIFICATION) {
        Box(modifier = Modifier.fillMaxSize()) {
            ActiveAlarmDashboard(
                alarm = alarm,
                onStop = { onFlowChange(AlarmFlow.DEPARTURE) },
                onSnooze = onDismiss,
            )
            VoiceNotificationOverlay(
                alarm = alarm,
                onDiscard = { onFlowChange(AlarmFlow.RINGING) },
                onSnooze = { onFlowChange(AlarmFlow.UPCOMING) },
            )
        }
        return
    }
    if (flow == AlarmFlow.UPCOMING) {
        UpcomingAlarmScreen(alarm = alarm, onDismiss = onDismiss)
        return
    }
    if (flow == AlarmFlow.POST_ALARM) {
        PostAlarmScreen(alarm = alarm, onStop = onStop, onDismiss = onDismiss)
        return
    }
    if (flow == AlarmFlow.EMPTY) {
        EmptyAlarmHomeScreen()
        return
    }
    if (flow == AlarmFlow.RINGING || flow == AlarmFlow.DEPARTURE) {
        ActiveAlarmDashboard(
            alarm = alarm,
                onStop = { onFlowChange(AlarmFlow.POST_ALARM) },
            onSnooze = { onFlowChange(AlarmFlow.VOICE_NOTIFICATION) },
        )
        return
    }

    val isDeparture = flow == AlarmFlow.DEPARTURE
    val title = when (flow) {
        AlarmFlow.RINGING -> R.string.flujo_sonando_titulo
        AlarmFlow.VOICE_NOTIFICATION -> R.string.flujo_voz_titulo
        AlarmFlow.POST_ALARM -> R.string.flujo_post_titulo
        AlarmFlow.DEPARTURE -> R.string.flujo_salida_titulo
        AlarmFlow.UPCOMING -> R.string.flujo_sonando_titulo
        AlarmFlow.EMPTY -> R.string.flujo_sonando_titulo
    }
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(if (isDeparture) R.drawable.ic_location_on else R.drawable.ic_alarm),
                    contentDescription = null,
                    tint = Coral,
                )
                Text(
                    text = stringResource(title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Cream,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 20.dp),
                )
                Text(
                    text = alarm.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 12.dp),
                )
                Text(
                    text = stringResource(
                        when (flow) {
                            AlarmFlow.DEPARTURE -> R.string.flujo_salida_texto
                            AlarmFlow.VOICE_NOTIFICATION -> R.string.flujo_voz_texto
                            AlarmFlow.POST_ALARM -> R.string.flujo_post_texto
                            AlarmFlow.RINGING -> R.string.flujo_sonando_texto
                            AlarmFlow.UPCOMING -> R.string.flujo_sonando_texto
                            AlarmFlow.EMPTY -> R.string.flujo_sonando_texto
                        },
                        alarm.schedule().departure,
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
            when (flow) {
                AlarmFlow.RINGING -> ActionButtons(R.string.flujo_ver_aviso_voz, R.string.flujo_detener, onAdvance, onDismiss)
                AlarmFlow.VOICE_NOTIFICATION -> ActionButtons(R.string.flujo_continuar, null, onAdvance, onDismiss)
                AlarmFlow.POST_ALARM -> ActionButtons(R.string.flujo_finalizar, null, onDismiss, onDismiss)
                AlarmFlow.DEPARTURE -> Unit
                AlarmFlow.UPCOMING -> Unit
                AlarmFlow.EMPTY -> Unit
            }
        }
    }
}

@Composable
private fun EmptyAlarmHomeScreen() {
    val now = java.time.LocalDateTime.now()
    Scaffold(containerColor = Moss) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 42.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    now.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")),
                    style = SmartWakeType.wakeTime.copy(fontSize = androidx.compose.ui.unit.TextUnit(60f, androidx.compose.ui.unit.TextUnitType.Sp)),
                    color = Cream,
                )
                Text(
                    now.format(java.time.format.DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", java.util.Locale("es")))
                        .replaceFirstChar { it.titlecase(java.util.Locale("es")) },
                    style = SmartWakeType.cardHeader,
                    color = Cream,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Surface(shape = CircleShape, color = Color(0xFF2D2721), modifier = Modifier.size(56.dp)) {}
                Surface(shape = CircleShape, color = Color(0xFF2D2721), modifier = Modifier.size(56.dp)) {}
            }
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun PostAlarmScreen(alarm: Alarm, onStop: () -> Unit, onDismiss: () -> Unit) {
    val schedule = alarm.schedule()
    val now = java.time.LocalDateTime.now()
    Scaffold(containerColor = Moss) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 42.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    now.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")),
                    style = SmartWakeType.wakeTime.copy(fontSize = androidx.compose.ui.unit.TextUnit(60f, androidx.compose.ui.unit.TextUnitType.Sp)),
                    color = Cream,
                )
                Text(
                    now.format(java.time.format.DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", java.util.Locale("es")))
                        .replaceFirstChar { it.titlecase(java.util.Locale("es")) },
                    style = SmartWakeType.cardHeader,
                    color = Cream,
                )
            }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 32.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF202D1D),
                border = androidx.compose.foundation.BorderStroke(1.dp, Bark),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 26.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Surface(shape = CircleShape, color = Bark, modifier = Modifier.size(40.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(painter = painterResource(R.drawable.ic_notifications), contentDescription = null, tint = Cream, modifier = Modifier.size(22.dp))
                            }
                        }
                        Text("SMARTWAKE • AHORA", style = SmartWakeType.cardHeader, color = Cream, modifier = Modifier.weight(1f))
                        Surface(
                            modifier = Modifier.widthIn(min = 142.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF2D2721),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Bark),
                        ) {
                            Text(
                                "TE QUEDAN 30 MIN",
                                style = SmartWakeType.fieldLabel,
                                color = Cream,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                            )
                        }
                    }
                    Text(
                        text = "Próximo paso: Salida a las ${schedule.departure.formatPadded()}",
                        style = SmartWakeType.sectionTitle,
                        color = Cream,
                    )
                    Text(
                        text = "Tráfico pesado en la ruta a ${alarm.destination?.name ?: "tu destino"}. Sal 10 min antes de lo previsto para llegar a las ${schedule.arrival.formatPadded()}.",
                        style = SmartWakeType.rowBody,
                        color = Cream,
                    )
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF2D2721),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Bark),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(painter = painterResource(R.drawable.ic_alarm), contentDescription = null, tint = Cream, modifier = Modifier.size(28.dp))
                            Text(
                                "Despertar: ${schedule.wake.formatPadded()} (Completado)",
                                style = SmartWakeType.rowTitle,
                                color = Cream,
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(18.dp),
                    ) {
                        OutlinedButton(
                            onClick = onStop,
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Cream),
                        ) { Text("Detener", style = SmartWakeType.cardHeader) }
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Coral),
                        ) {
                            Icon(painter = painterResource(R.drawable.ic_alarm), contentDescription = null, modifier = Modifier.size(20.dp))
                            Text("Posponer 10m", modifier = Modifier.padding(start = 6.dp), style = SmartWakeType.cardHeader)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Surface(shape = CircleShape, color = Color(0xFF2D2721), modifier = Modifier.size(56.dp)) {}
                Surface(shape = CircleShape, color = Color(0xFF2D2721), modifier = Modifier.size(56.dp)) {}
            }
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun VoiceNotificationOverlay(
    alarm: Alarm,
    onDiscard: () -> Unit,
    onSnooze: () -> Unit,
) {
    val schedule = alarm.schedule()
    val delayedArrival = schedule.arrival.plusMinutes(17)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.42f)),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 390.dp)
                .fillMaxWidth()
                .height(494.dp),
            shape = RoundedCornerShape(16.dp),
            color = Moss,
            border = androidx.compose.foundation.BorderStroke(1.dp, Bark),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(shape = CircleShape, color = Bark, modifier = Modifier.size(54.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(R.drawable.ic_notifications),
                                contentDescription = null,
                                tint = Coral,
                                modifier = Modifier.size(30.dp),
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "SMARTWAKE AUDIO WARNING",
                            style = SmartWakeType.cardHeader,
                            color = Coral,
                        )
                        Text(
                            text = "Aviso por Voz Activo",
                            style = SmartWakeType.rowBody,
                            color = Cream.copy(alpha = 0.72f),
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Ember,
                        border = androidx.compose.foundation.BorderStroke(2.dp, Coral),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("!", style = SmartWakeType.cardHeader, color = Cream)
                            Text("Impacto", style = SmartWakeType.cardHeader, color = Cream)
                        }
                    }
                }
                Text(
                    text = "Consecuencia detectada",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Cream,
                )
                Text(
                    text = "Si pospones 10 minutos, tu llegada estimada a '${alarm.destination?.name ?: "tu destino"}' pasará de las ${schedule.arrival.formatPadded()} a las ${delayedArrival.formatPadded()}, reduciendo tu margen de seguridad ante el tráfico pesado.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Cream.copy(alpha = 0.72f),
                )
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Moss,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Bark),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TimeColumn("HORA\nPLANEADA", schedule.arrival.formatPadded())
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Ember,
                            border = androidx.compose.foundation.BorderStroke(2.dp, Coral),
                        ) {
                            Text("+17 min", style = MaterialTheme.typography.titleLarge, color = Cream, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
                        }
                        TimeColumn("NUEVA ESTIMACIÓN", delayedArrival.formatPadded())
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    OutlinedButton(
                        onClick = onDiscard,
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Cream),
                    ) { Text("Descartar") }
                    Button(
                        onClick = onSnooze,
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Coral),
                    ) {
                        Icon(painter = painterResource(R.drawable.ic_alarm), contentDescription = null, modifier = Modifier.size(20.dp))
                        Text("Posponer", modifier = Modifier.padding(start = 6.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun UpcomingAlarmScreen(alarm: Alarm, onDismiss: () -> Unit) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(R.drawable.ic_alarm),
                    contentDescription = null,
                    tint = Coral,
                    modifier = Modifier.size(32.dp),
                )
                Text(
                    text = "Próximamente",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Cream,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 20.dp),
                )
                Text(
                    text = alarm.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = Cream,
                    modifier = Modifier.padding(top = 12.dp),
                )
                Text(
                    text = "Te avisaremos nuevamente en 10 minutos.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Cream.copy(alpha = 0.72f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Coral),
            ) {
                Text("Volver a la alarma")
            }
        }
    }
}

@Composable
private fun TimeColumn(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = SmartWakeType.fieldLabel, color = Cream.copy(alpha = 0.72f))
        Text(value, style = SmartWakeType.bigValue, color = Cream)
    }
}

@Composable
private fun ActiveAlarmDashboard(
    alarm: Alarm,
    onStop: () -> Unit,
    onSnooze: () -> Unit,
) {
    val schedule = alarm.schedule()
    val travelMinutes = schedule.totalTravelMinutes

    Scaffold(containerColor = Moss) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_alarm),
                        contentDescription = null,
                        tint = Coral,
                        modifier = Modifier.size(22.dp),
                    )
                    Text("SmartWake Activo", style = SmartWakeType.cardHeader, color = Cream)
                }
                Text(
                    text = "Destino: ${alarm.destination?.name ?: "Sin destino"}",
                    style = SmartWakeType.arrival,
                    color = Cream.copy(alpha = 0.72f),
                    modifier = Modifier.padding(top = 48.dp),
                )
                Box(
                    modifier = Modifier.padding(top = 26.dp).size(274.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = CircleShape,
                        color = Moss,
                        border = androidx.compose.foundation.BorderStroke(2.dp, Bark),
                    ) {}
                    Text(
                        text = schedule.wake.formatPadded(),
                        style = SmartWakeType.wakeTime.copy(fontSize = androidx.compose.ui.unit.TextUnit(60f, androidx.compose.ui.unit.TextUnitType.Sp)),
                        color = Cream,
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 28.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                InfoCard(
                    icon = R.drawable.ic_directions_car,
                    title = "Tráfico pesado hoy",
                    body = "Sal de casa según lo planeado para llegar a tiempo.",
                )
                InfoCard(
                    icon = R.drawable.ic_clock,
                    title = "Saldrás en $travelMinutes minutos",
                    body = "Te avisaremos cuando sea el momento de partir.",
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            HorizontalDivider(color = Bark)
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Button(
                    onClick = onStop,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Coral, contentColor = Moss),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Text("Detener", modifier = Modifier.padding(start = 8.dp))
                }
                OutlinedButton(
                    onClick = onSnooze,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Cream),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_alarm),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Text("Posponer por 10 min", modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}

@Composable
private fun InfoCard(icon: Int, title: String, body: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MossLight.copy(alpha = 0.52f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Bark.copy(alpha = 0.6f)),
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Surface(shape = CircleShape, color = Bark, modifier = Modifier.size(42.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(painter = painterResource(icon), contentDescription = null, tint = Coral, modifier = Modifier.size(22.dp))
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = SmartWakeType.cardHeader, color = Cream)
                Text(body, style = SmartWakeType.rowBody, color = Cream.copy(alpha = 0.72f))
            }
        }
    }
}

@Composable
private fun ActionButtons(primary: Int, secondary: Int?, onPrimary: () -> Unit, onSecondary: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = onPrimary,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Coral),
        ) { Text(stringResource(primary)) }
        if (secondary != null) {
            OutlinedButton(onClick = onSecondary, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(secondary))
            }
        }
    }
}
