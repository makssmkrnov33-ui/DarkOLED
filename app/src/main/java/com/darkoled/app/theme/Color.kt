package com.darkoled.app.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val AccentBlue = Color(0xFF0084FF)
val AccentPurple = Color(0xFF7C4DFF)
val AccentPink = Color(0xFFFF4081)
val AccentTeal = Color(0xFF00E5FF)
val AccentOrange = Color(0xFFFF6D00)
val AccentGreen = Color(0xFF00E676)

val LightColors = lightColorScheme(
    primary = AccentBlue,
    secondary = AccentPurple,
    tertiary = AccentPink,
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    surfaceVariant = Color(0xFFE8E8E8),
    onBackground = Color(0xFF1A1A1A),
    onSurface = Color(0xFF1A1A1A)
)

val DarkColors = darkColorScheme(
    primary = AccentBlue,
    secondary = AccentPurple,
    tertiary = AccentPink,
    background = Color.Black,
    surface = Color(0xFF121212),
    surfaceVariant = Color(0xFF1E1E1E),
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFB0B0B0)
)
