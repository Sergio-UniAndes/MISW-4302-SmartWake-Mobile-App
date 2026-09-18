package com.smartwake.app.ui.crear

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smartwake.app.R
import com.smartwake.app.data.PrepTask
import com.smartwake.app.data.PrepTasks
import com.smartwake.app.ui.components.IconContainer
import com.smartwake.app.ui.components.SmartWakeSwitch
import com.smartwake.app.ui.components.TopBarContainer
import com.smartwake.app.ui.components.outlineBorder
import com.smartwake.app.ui.theme.Cream
import com.smartwake.app.ui.theme.SmartWakeTheme
import com.smartwake.app.ui.theme.SmartWakeType

private const val StepMinutes = 5
private const val MaxMinutes = 240
private val CardShape = RoundedCornerShape(12.dp)

/**
 * Figma: "Vista Tiempo de Preparación (Dark)" (267:860).
 * The total is the sum of the tasks that are on; −/+ fine-tunes it in 5-minute steps.
 * Changes only reach the alarm when confirmed with the check button.
 */
@Composable
fun TiempoPreparacionScreen(
    initialTaskIds: Set<String>,
    initialMinutes: Int,
    onBack: () -> Unit,
    onConfirm: (taskIds: Set<String>, minutes: Int) -> Unit,
) {
    var taskIds: List<String> by rememberSaveable { mutableStateOf(ArrayList(initialTaskIds)) }
    var minutes by rememberSaveable { mutableIntStateOf(initialMinutes) }

    fun toggle(task: PrepTask, on: Boolean) {
        taskIds = ArrayList(if (on) taskIds + task.id else taskIds - task.id) // ArrayList so it can be saved
        minutes = (minutes + if (on) task.minutes else -task.minutes).coerceIn(0, MaxMinutes)
    }

    Scaffold(
        topBar = { PrepTopBar(onBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onConfirm(taskIds.toSet(), minutes) },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Cream,
            ) {
                Icon(painterResource(R.drawable.ic_check), contentDescription = stringResource(R.string.prep_listo))
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
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
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionTitle(stringResource(R.string.prep_tiempo_fijo))
                MinutesStepper(
                    minutes = minutes,
                    onMinutesChange = { minutes = it.coerceIn(0, MaxMinutes) },
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionTitle(stringResource(R.string.prep_colecciones))
                PrepTasks.all.forEach { task ->
                    TaskCard(task = task, checked = task.id in taskIds, onCheckedChange = { toggle(task, it) })
                }
            }
            InfoNote()
            // Room for the FAB, like the Figma fab-spacer.
            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun PrepTopBar(onBack: () -> Unit) {
    TopBarContainer(color = MaterialTheme.colorScheme.surface) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                onClick = onBack,
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                border = outlineBorder(),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_left),
                        contentDescription = stringResource(R.string.accion_volver),
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            Text(
                text = stringResource(R.string.prep_titulo),
                style = SmartWakeType.sectionTitle,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.size(40.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = SmartWakeType.sectionTitle, color = MaterialTheme.colorScheme.onBackground)
}

@Composable
private fun MinutesStepper(minutes: Int, onMinutesChange: (Int) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        color = MaterialTheme.colorScheme.surface,
        border = outlineBorder(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.prep_establecer),
                style = SmartWakeType.rowBody,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StepButton(
                    icon = R.drawable.ic_minus,
                    label = stringResource(R.string.prep_restar),
                    enabled = minutes > 0,
                    onClick = { onMinutesChange(minutes - StepMinutes) },
                )
                Text(
                    text = minutes.toString(),
                    style = SmartWakeType.stepperValue,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(min = 36.dp),
                )
                StepButton(
                    icon = R.drawable.ic_plus,
                    label = stringResource(R.string.prep_sumar),
                    enabled = minutes < MaxMinutes,
                    onClick = { onMinutesChange(minutes + StepMinutes) },
                )
            }
        }
    }
}

@Composable
private fun StepButton(@DrawableRes icon: Int, label: String, enabled: Boolean, onClick: () -> Unit) {
    FilledIconButton(
        onClick = onClick,
        enabled = enabled,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Cream,
        ),
    ) {
        Icon(painterResource(icon), contentDescription = label, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun TaskCard(task: PrepTask, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        color = MaterialTheme.colorScheme.surface,
        border = outlineBorder(),
    ) {
        Row(
            modifier = Modifier
                .toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconContainer(icon = task.icon)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(task.name, style = SmartWakeType.sectionTitle, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    text = stringResource(R.string.minutos, task.minutes),
                    style = SmartWakeType.rowBody,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            // Handled by the card's toggleable so the whole row is one touch target.
            SmartWakeSwitch(checked = checked, onCheckedChange = null)
        }
    }
}

@Composable
private fun InfoNote() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        color = MaterialTheme.colorScheme.surface,
        border = outlineBorder(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconContainer(icon = R.drawable.ic_info, size = 32.dp, iconSize = 18.dp)
            Text(
                text = stringResource(R.string.prep_nota),
                style = SmartWakeType.rowBody,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Preview(widthDp = 400, heightDp = 900)
@Composable
private fun TiempoPreparacionPreview() {
    SmartWakeTheme {
        TiempoPreparacionScreen(
            initialTaskIds = PrepTasks.defaultEnabledIds,
            initialMinutes = 45,
            onBack = {},
            onConfirm = { _, _ -> },
        )
    }
}
