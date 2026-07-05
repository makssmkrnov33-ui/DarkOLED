package com.darkoled.app.ui.voice

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

@Composable
fun WaveformView(amplitudes: List<Int>, modifier: Modifier = Modifier, color: Color = Color(0xFF0084FF)) {
    Canvas(modifier = modifier) {
        val barWidth = size.width / amplitudes.size.coerceAtLeast(1)
        val maxAmp = amplitudes.maxOrNull()?.coerceAtLeast(1) ?: 1

        amplitudes.forEachIndexed { index, amp ->
            val barHeight = (amp.toFloat() / maxAmp) * size.height
            drawRect(
                color = color,
                topLeft = Offset(index * barWidth, size.height - barHeight),
                size = Size(barWidth * 0.8f, barHeight)
            )
        }
    }
}
