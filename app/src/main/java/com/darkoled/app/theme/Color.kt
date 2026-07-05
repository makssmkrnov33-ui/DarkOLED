package com.darkoled.app.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val LightColors = lightColorScheme(
    primary = Color(0xFF0084FF),
    background = Color(0xFFF5F5F5),
    surface = Color.White
)

val DarkColors = darkColorScheme(
    primary = Color(0xFF0084FF),
    background = Color.Black,
    surface = Color(0xFF121212),
    onBackground = Color.White,
    onSurface = Color.White
)
