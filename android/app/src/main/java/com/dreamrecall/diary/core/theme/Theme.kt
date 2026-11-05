package com.dreamrecall.diary.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NightScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = SurfaceSecondary,
    onPrimaryContainer = TextPrimary,
    secondary = Accent,
    onSecondary = NightDeep,
    secondaryContainer = SurfaceCard,
    onSecondaryContainer = TextBody,
    tertiary = AccentCool,
    onTertiary = NightDeep,
    background = NightDeep,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceSecondary,
    onSurfaceVariant = TextMuted,
    outline = BorderStrong,
    outlineVariant = BorderSoft,
    error = Danger,
    onError = NightDeep
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NightScheme,
        typography = AppTypography,
        content = content
    )
}
