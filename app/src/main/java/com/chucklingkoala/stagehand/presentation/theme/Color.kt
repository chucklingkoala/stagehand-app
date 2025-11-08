package com.chucklingkoala.stagehand.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * Stagehand Color Palette
 * Based on the web app's dark theme with easy customization
 */
object StagehandColors {
    // Dark Theme Colors (Primary theme for v1)
    val BackgroundPrimary = Color(0xFF1A1A1A)      // --bg-primary
    val BackgroundSecondary = Color(0xFF2D2D2D)    // --bg-secondary
    val TextPrimary = Color(0xFFE0E0E0)            // --text-primary
    val TextSecondary = Color(0xFFA0A0A0)          // --text-secondary
    val BorderColor = Color(0xFF404040)            // --border-color
    val CardBackground = Color(0xFF2D2D2D)         // --card-bg
    val InputBackground = Color(0xFF3A3A3A)        // --input-bg
    val AccentColor = Color(0xFF7289DA)            // --accent-color (Discord purple)
    val LinkColor = Color(0xFF66B3FF)              // --link-color

    // Status Colors
    val OnShowGreen = Color(0xFF57F287)
    val DumpRed = Color(0xFFED4245)
    val DuplicateOrange = Color(0xFFFAA61A)

    // Button Colors
    val ButtonPrimary = Color(0xFF5865F2)          // Discord blurple
    val ButtonSuccess = Color(0xFF57F287)
    val ButtonDanger = Color(0xFFED4245)
    val ButtonSecondary = Color(0xFF4F545C)

    // Light Theme Colors (for future use)
    val LightBackgroundPrimary = Color(0xFFF5F5F5)
    val LightBackgroundSecondary = Color(0xFFFFFFFF)
    val LightTextPrimary = Color(0xFF333333)
    val LightTextSecondary = Color(0xFF666666)
}
