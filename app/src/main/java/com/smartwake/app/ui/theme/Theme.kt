package com.smartwake.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SmartWakeDarkColorScheme = darkColorScheme(
    primary = Coral,
    onPrimary = White,
    primaryContainer = Ember,
    onPrimaryContainer = Blush,
    background = Night,
    onBackground = Cream,
    surface = Moss,
    onSurface = Cream,
    surfaceVariant = MossLight,
    onSurfaceVariant = Cream,
    surfaceContainerHighest = MossLight,
    outline = Bark,
    outlineVariant = Bark,
)

@Composable
fun SmartWakeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SmartWakeDarkColorScheme,
        typography = Typography,
        content = content,
    )
}

/**
 * For Material 3 Design Kit components (dialogs, time picker, text field), which the
 * Figma file leaves in Roboto instead of Plus Jakarta Sans.
 */
@Composable
fun KitTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme,
        typography = KitTypography,
        content = content,
    )
}
