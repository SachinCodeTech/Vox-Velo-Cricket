package com.example.voxvelo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = VvAccent,
    onPrimary = VvText,
    primaryContainer = VvAccentDim,
    onPrimaryContainer = VvText,
    secondary = VvTeal,
    onSecondary = VvBg,
    secondaryContainer = VvTealDim,
    onSecondaryContainer = VvText,
    tertiary = VvAmber,
    onTertiary = VvBg,
    background = VvBg,
    onBackground = VvText,
    surface = VvSurface,
    onSurface = VvText,
    surfaceVariant = VvSurface2,
    onSurfaceVariant = VvTextDim,
    outline = VvLineStrong,
    outlineVariant = VvLine
)

@Composable
fun VoxVeloTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
