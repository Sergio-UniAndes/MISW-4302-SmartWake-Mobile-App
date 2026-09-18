package com.smartwake.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.smartwake.app.R
import com.smartwake.app.ui.theme.KitTypography
import com.smartwake.app.ui.theme.Mist
import com.smartwake.app.ui.theme.Slate

@Composable
fun outlineBorder() = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)

/** Shell for the custom Figma app bars: fills behind the status bar, 64dp tall, bottom border. */
@Composable
fun TopBarContainer(
    color: Color,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val borderColor = MaterialTheme.colorScheme.outline
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color)
            .drawBehind {
                val y = size.height - 0.5.dp.toPx()
                drawLine(borderColor, Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
            }
            .windowInsetsPadding(WindowInsets.statusBars)
            .height(64.dp),
        content = content,
    )
}

/** Figma "App bar" (Small-centered) from the Material 3 kit, with a back arrow. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitTopAppBar(title: String, containerColor: Color, onBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text(title, style = KitTypography.titleLarge) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = stringResource(R.string.accion_volver),
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = containerColor,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
    )
}

/** Bottom area holding a screen's main action, above the navigation bar. */
@Composable
fun BottomActionBar(color: Color, content: @Composable BoxScope.() -> Unit) {
    val borderColor = MaterialTheme.colorScheme.outline
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color)
            .drawBehind { drawLine(borderColor, Offset.Zero, Offset(size.width, 0f), 1.dp.toPx()) }
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 24.dp),
        content = content,
    )
}

/** Full-width action button; the Figma instances fill their whole frame, so the corners are square. */
@Composable
fun BlockButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        enabled = enabled,
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = contentColor,
        ),
    ) {
        Text(text, style = KitTypography.labelLarge)
    }
}

/** An icon centered in a filled (optionally outlined) shape, as used across the Figma cards. */
@Composable
fun IconContainer(
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    iconSize: Dp = 20.dp,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    bordered: Boolean = true,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    shape: Shape = CircleShape,
) {
    Surface(
        modifier = modifier.size(size),
        shape = shape,
        color = containerColor,
        border = if (bordered) outlineBorder() else null,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(iconSize),
            )
        }
    }
}

@Composable
fun SmartWakeSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        colors = SwitchDefaults.colors(
            uncheckedThumbColor = Slate,
            uncheckedTrackColor = Mist,
            uncheckedBorderColor = Slate,
        ),
    )
}

@Composable
fun InsetDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier.padding(start = 16.dp),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outline,
    )
}

/** The logo in its 160dp circle, with the Figma wrapper's 16dp vertical padding. */
@Composable
fun SmartWakeLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.size(160.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = outlineBorder(),
        ) {
            Image(
                painter = painterResource(R.drawable.logo_smartwake),
                contentDescription = stringResource(R.string.logo_description),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
