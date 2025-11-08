package com.chucklingkoala.stagehand.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = StagehandColors.AccentColor,
    onPrimary = StagehandColors.TextPrimary,
    secondary = StagehandColors.ButtonSecondary,
    onSecondary = StagehandColors.TextPrimary,
    background = StagehandColors.BackgroundPrimary,
    onBackground = StagehandColors.TextPrimary,
    surface = StagehandColors.CardBackground,
    onSurface = StagehandColors.TextPrimary,
    error = StagehandColors.DumpRed,
    onError = StagehandColors.TextPrimary,
    outline = StagehandColors.BorderColor,
    surfaceVariant = StagehandColors.BackgroundSecondary,
    onSurfaceVariant = StagehandColors.TextSecondary,
)

@Composable
fun StagehandTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = StagehandTypography,
        content = content
    )
}
