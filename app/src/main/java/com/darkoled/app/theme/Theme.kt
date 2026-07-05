package com.darkoled.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class ThemeMode { LIGHT, DARK, AUTO }

@Composable
fun DarkOledTheme(
    themeMode: ThemeMode = ThemeMode.AUTO,
    accentColor: Color = Color(0xFF0084FF),
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        ThemeMode.LIGHT -> LightColors.copy(primary = accentColor)
        ThemeMode.DARK -> DarkColors.copy(primary = accentColor)
        ThemeMode.AUTO -> if (isSystemInDarkTheme()) DarkColors.copy(primary = accentColor)
                          else LightColors.copy(primary = accentColor)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
