package com.darkoled.app.ui.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun GlassHomeIcon(selected: Boolean, modifier: Modifier = Modifier) {
    val glowAlpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0.4f, animationSpec = tween(300), label = "glow"
    )
    Canvas(modifier = modifier.size(28.dp)) {
        val c = center
        val r = size.minDimension / 2

        drawCircle(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0x664D7FFF), Color(0x33FF4466)),
                startY = 0f, endY = size.height
            ),
            radius = r, center = c
        )
        drawCircle(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color(0x44FF4466)),
                startY = size.height * 0.5f, endY = size.height
            ),
            radius = r, center = c
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.3f * glowAlpha),
            radius = r * 0.08f,
            center = Offset(c.x - r * 0.3f, c.y - r * 0.3f)
        )

        val hw = size.width * 0.35f
        val hh = size.width * 0.3f
        val roof = Path().apply {
            moveTo(c.x, c.y - hh * 1.1f)
            lineTo(c.x + hw, c.y - hh * 0.2f)
            lineTo(c.x - hw, c.y - hh * 0.2f)
            close()
        }
        drawPath(roof, Color.White.copy(alpha = 0.8f))

        val bodyW = hw * 0.6f
        val bodyH = hh * 0.9f
        drawRect(
            color = Color.White.copy(alpha = 0.7f),
            topLeft = Offset(c.x - bodyW, c.y - hh * 0.1f),
            size = Size(bodyW * 2, bodyH)
        )
    }
}

@Composable
fun GlassChatIcon(selected: Boolean, modifier: Modifier = Modifier) {
    val glowAlpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0.4f, animationSpec = tween(300), label = "glow"
    )
    Canvas(modifier = modifier.size(28.dp)) {
        val c = center
        val r = size.minDimension / 2

        drawCircle(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF1A237E), Color(0xFFE91E63)),
                startY = 0f, endY = size.height
            ),
            radius = r, center = c
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.15f * glowAlpha),
            radius = r * 0.06f,
            center = Offset(c.x - r * 0.3f, c.y - r * 0.3f)
        )

        val bw = size.width * 0.5f
        val bh = size.width * 0.35f
        val bubble = Path().apply {
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    left = c.x - bw, top = c.y - bh * 0.7f,
                    right = c.x + bw, bottom = c.y + bh * 0.5f,
                    radiusX = bw * 0.25f, radiusY = bw * 0.25f
                )
            )
            moveTo(c.x - bw * 0.2f, c.y + bh * 0.4f)
            lineTo(c.x - bw * 0.4f, c.y + bh * 0.8f)
            lineTo(c.x, c.y + bh * 0.4f)
        }
        drawPath(bubble, Color.White.copy(alpha = 0.85f))

        val dotSize = bw * 0.1f
        drawCircle(Color.White, dotSize, Offset(c.x - bw * 0.35f, c.y - bh * 0.1f))
        drawCircle(Color.White, dotSize, Offset(c.x, c.y - bh * 0.1f))
        drawCircle(Color.White, dotSize, Offset(c.x + bw * 0.35f, c.y - bh * 0.1f))

        if (selected) {
            drawCircle(Color(0xFFFF4081), bw * 0.18f, Offset(c.x + bw * 0.5f, c.y - bh * 0.5f))
            drawCircle(Color.White, bw * 0.08f, Offset(c.x + bw * 0.5f, c.y - bh * 0.5f))
        }
    }
}

@Composable
fun GlassNewsIcon(selected: Boolean, modifier: Modifier = Modifier) {
    val glowAlpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0.4f, animationSpec = tween(300), label = "glow"
    )
    Canvas(modifier = modifier.size(28.dp)) {
        val c = center
        val r = size.minDimension / 2

        drawCircle(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF1A237E), Color(0xFF7C4DFF)),
                startY = 0f, endY = size.height
            ),
            radius = r, center = c
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.12f * glowAlpha),
            radius = r * 0.06f,
            center = Offset(c.x - r * 0.3f, c.y - r * 0.3f)
        )

        val pw = size.width * 0.15f
        val ph = size.width * 0.18f
        val px = c.x - size.width * 0.38f
        val py = c.y - ph * 0.5f
        drawRoundRect(Color.White.copy(alpha = 0.8f), Offset(px, py), Size(pw * 1.1f, ph), CornerRadius(2f, 2f))

        val tx = px + pw * 1.3f
        for (i in 0..3) {
            val lineW = (0.6f - i * 0.1f) * size.width
            drawRect(Color.White.copy(alpha = 0.6f - i * 0.1f), Offset(tx, py + i * (ph * 0.22f)), Size(lineW, 2f))
        }

        val imgY = py + ph * 1.2f
        drawRoundRect(Color.White.copy(alpha = 0.5f), Offset(px, imgY), Size(pw * 1.1f, ph * 0.8f), CornerRadius(2f, 2f))
        val tx2 = px + pw * 1.3f
        for (i in 0..2) {
            drawRect(Color.White.copy(alpha = 0.4f), Offset(tx2, imgY + i * (ph * 0.25f)), Size(pw * 1.8f, 2f))
        }
    }
}

@Composable
fun GlassProfileIcon(selected: Boolean, modifier: Modifier = Modifier) {
    val glowAlpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0.4f, animationSpec = tween(300), label = "glow"
    )
    Canvas(modifier = modifier.size(28.dp)) {
        val c = center
        val r = size.minDimension / 2

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF1A237E), Color(0xFFE91E63)),
                center = c, radius = r
            ),
            radius = r, center = c
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.15f * glowAlpha),
            radius = r * 0.08f,
            center = Offset(c.x - r * 0.25f, c.y - r * 0.25f)
        )

        val headR = size.width * 0.14f
        drawCircle(Color.White.copy(alpha = 0.8f), headR, Offset(c.x, c.y - size.width * 0.1f))

        val bodyPath = Path().apply {
            moveTo(c.x - size.width * 0.3f, c.y + size.width * 0.15f)
            quadraticBezierTo(c.x, c.y + size.width * 0.35f, c.x + size.width * 0.3f, c.y + size.width * 0.15f)
            quadraticBezierTo(c.x + size.width * 0.15f, c.y + size.width * 0.2f, c.x, c.y + size.width * 0.18f)
            quadraticBezierTo(c.x - size.width * 0.15f, c.y + size.width * 0.2f, c.x - size.width * 0.3f, c.y + size.width * 0.15f)
            close()
        }
        drawPath(bodyPath, Color.White.copy(alpha = 0.7f))
    }
}
