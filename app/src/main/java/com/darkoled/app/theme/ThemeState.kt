package com.darkoled.app.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.darkoled.app.model.ChatTheme

class ThemeState {
    var themeMode by mutableStateOf(ThemeMode.AUTO)
    var accentColor by mutableStateOf(AccentBlue)
    var accentPreset by mutableStateOf(AccentPreset.BLUE)
    var chatTheme by mutableStateOf(ChatTheme.PINK_GRADIENT)
}

val LocalThemeState = compositionLocalOf { ThemeState() }
