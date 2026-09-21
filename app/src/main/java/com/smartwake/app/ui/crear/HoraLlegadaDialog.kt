package com.smartwake.app.ui.crear

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.smartwake.app.R
import com.smartwake.app.ui.theme.Blush
import com.smartwake.app.ui.theme.Cream
import com.smartwake.app.ui.theme.Ember
import com.smartwake.app.ui.theme.KitTheme
import com.smartwake.app.ui.theme.White
import java.time.LocalTime

/** Figma: "Vista Hora de Llegada - Dialog (Dark)" (267:966), the Material 3 dial time picker. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HoraLlegadaDialog(initial: LocalTime, onConfirm: (LocalTime) -> Unit, onDismiss: () -> Unit) {
    val state = rememberTimePickerState(initialHour = initial.hour, initialMinute = initial.minute, is24Hour = false)
    val colors = MaterialTheme.colorScheme

    KitTheme {
        Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Surface(
                modifier = Modifier.width(328.dp),
                shape = RoundedCornerShape(28.dp),
                color = colors.surface,
            ) {
                Column(modifier = Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 16.dp)) {
                    Text(
                        text = stringResource(R.string.hora_llegada_titulo),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onSurface,
                        modifier = Modifier.padding(bottom = 20.dp),
                    )
                    TimePicker(
                        state = state,
                        layoutType = TimePickerLayoutType.Vertical,
                        colors = TimePickerDefaults.colors(
                            clockDialColor = colors.surface,
                            clockDialSelectedContentColor = White,
                            clockDialUnselectedContentColor = Cream,
                            selectorColor = colors.primary,
                            containerColor = colors.surface,
                            periodSelectorBorderColor = colors.outline,
                            periodSelectorSelectedContainerColor = Ember,
                            periodSelectorUnselectedContainerColor = colors.surface,
                            periodSelectorSelectedContentColor = Blush,
                            periodSelectorUnselectedContentColor = Cream,
                            timeSelectorSelectedContainerColor = Ember,
                            timeSelectorUnselectedContainerColor = colors.surfaceVariant,
                            timeSelectorSelectedContentColor = Blush,
                            timeSelectorUnselectedContentColor = Cream,
                        ),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    ) {
                        val buttonColors = ButtonDefaults.textButtonColors(contentColor = colors.primary)
                        TextButton(onClick = onDismiss, colors = buttonColors) {
                            Text(stringResource(R.string.accion_cancelar))
                        }
                        TextButton(
                            onClick = { onConfirm(LocalTime.of(state.hour, state.minute)) },
                            colors = buttonColors,
                        ) {
                            Text(stringResource(R.string.accion_confirmar))
                        }
                    }
                }
            }
        }
    }
}
