package com.darkoled.app.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppTypography = Typography(
    headlineLarge = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.SansSerif),
    headlineMedium = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.SansSerif),
    bodyLarge = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal, fontFamily = FontFamily.SansSerif),
    bodyMedium = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, fontFamily = FontFamily.SansSerif),
    labelMedium = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.SansSerif)
)
