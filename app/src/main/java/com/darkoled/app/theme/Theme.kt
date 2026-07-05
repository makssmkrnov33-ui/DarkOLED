package com.darkoled.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class ThemeMode { LIGHT, DARK, AUTO }

enum class AccentPreset(val label: String, val color: Color) {
    BLUE("Blue", AccentBlue),
    PURPLE("Purple", AccentPurple),
    PINK("Pink", AccentPink),
    TEAL("Teal", AccentTeal),
    ORANGE("Orange", AccentOrange),
    GREEN("Green", AccentGreen)
}

@Composable
fun DarkOledTheme(
    themeMode: ThemeMode = ThemeMode.AUTO,
    accentColor: Color = AccentBlue,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        ThemeMode.LIGHT -> LightColors.copy(
            primary = accentColor,
            secondary = AccentPurple,
            tertiary = AccentPink
        )
        ThemeMode.DARK -> DarkColors.copy(
            primary = accentColor,
            secondary = AccentPurple,
            tertiary = AccentPink
        )
        ThemeMode.AUTO -> if (isSystemInDarkTheme()) DarkColors.copy(
            primary = accentColor,
            secondary = AccentPurple,
            tertiary = AccentPink
        ) else LightColors.copy(
            primary = accentColor,
            secondary = AccentPurple,
            tertiary = AccentPink
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
