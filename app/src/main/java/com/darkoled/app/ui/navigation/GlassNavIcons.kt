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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun GlassHomeIcon(selected: Boolean, modifier: Modifier = Modifier) {
    val a by animateFloatAsState(if (selected) 1f else 0.3f, tween(400), label = "g")
    Canvas(modifier = modifier.size(28.dp)) {
        val c = center; val r = size.minDimension / 2
        drawCircle(Brush.verticalGradient(listOf(Color(0xFF0F3460), Color(0xFF533483)), startY = 0f, endY = size.height), radius = r, center = c)
        if (selected) drawCircle(Color(0xFFE94560).copy(alpha = 0.25f), radius = r * 0.9f, center = c)
        drawCircle(Color.White.copy(alpha = 0.08f), radius = r, center = c)
        drawCircle(Color.White.copy(alpha = 0.35f * a), radius = r * 0.05f, center = Offset(c.x - r * 0.3f, c.y - r * 0.3f))
        val hw = size.width * 0.35f; val hh = size.width * 0.3f
        val roof = Path().apply { moveTo(c.x, c.y - hh * 1.1f); lineTo(c.x + hw, c.y - hh * 0.15f); lineTo(c.x - hw, c.y - hh * 0.15f); close() }
        drawPath(roof, Color.White.copy(alpha = 0.85f))
        if (selected) drawPath(roof, Color(0xFFE94560).copy(alpha = 0.4f), style = Stroke(width = 1.5f))
        val bodyW = hw * 0.6f; val bodyH = hh * 0.9f
        drawRect(Color.White.copy(alpha = 0.75f), Offset(c.x - bodyW, c.y - hh * 0.05f), Size(bodyW * 2, bodyH))
        drawRect(Color(0xFFE94560).copy(alpha = 0.3f * a), Offset(c.x - bodyW + 1f, c.y - hh * 0.05f + 1f), Size(bodyW * 2 - 2f, bodyH - 2f), style = Stroke(1f))
    }
}

@Composable
fun GlassChatIcon(selected: Boolean, modifier: Modifier = Modifier) {
    val a by animateFloatAsState(if (selected) 1f else 0.3f, tween(400), label = "g")
    Canvas(modifier = modifier.size(28.dp)) {
        val c = center; val r = size.minDimension / 2
        drawCircle(Brush.verticalGradient(listOf(Color(0xFF1A1A2E), Color(0xFFE94560)), startY = 0f, endY = size.height), radius = r, center = c)
        if (selected) drawCircle(Color(0xFFFF6B6B).copy(alpha = 0.2f), radius = r * 0.92f, center = c)
        drawCircle(Color.White.copy(alpha = 0.18f * a), radius = r * 0.05f, center = Offset(c.x - r * 0.3f, c.y - r * 0.3f))
        val bw = size.width * 0.5f; val bh = size.width * 0.35f
        val bubble = Path().apply {
            addRoundRect(androidx.compose.ui.geometry.RoundRect(c.x - bw, c.y - bh * 0.7f, c.x + bw, c.y + bh * 0.5f, bw * 0.25f, bw * 0.25f))
            moveTo(c.x - bw * 0.2f, c.y + bh * 0.4f); lineTo(c.x - bw * 0.4f, c.y + bh * 0.8f); lineTo(c.x, c.y + bh * 0.4f)
        }
        drawPath(bubble, Color.White.copy(alpha = 0.85f))
        if (selected) drawPath(bubble, Color(0xFFFF6B6B).copy(alpha = 0.5f), style = Stroke(1.2f))
        val ds = bw * 0.1f
        drawCircle(Color.White, ds, Offset(c.x - bw * 0.35f, c.y - bh * 0.1f))
        drawCircle(Color.White, ds, Offset(c.x, c.y - bh * 0.1f))
        drawCircle(Color.White, ds, Offset(c.x + bw * 0.35f, c.y - bh * 0.1f))
        if (selected) {
            drawCircle(Color(0xFFFF6B6B), bw * 0.2f, Offset(c.x + bw * 0.5f, c.y - bh * 0.5f))
            drawCircle(Color.White, bw * 0.08f, Offset(c.x + bw * 0.5f, c.y - bh * 0.5f))
        }
    }
}

@Composable
fun GlassNewsIcon(selected: Boolean, modifier: Modifier = Modifier) {
    val a by animateFloatAsState(if (selected) 1f else 0.3f, tween(400), label = "g")
    Canvas(modifier = modifier.size(28.dp)) {
        val c = center; val r = size.minDimension / 2
        drawCircle(Brush.verticalGradient(listOf(Color(0xFF16213E), Color(0xFF0F3460)), startY = 0f, endY = size.height), radius = r, center = c)
        if (selected) drawCircle(Color(0xFF533483).copy(alpha = 0.3f), radius = r * 0.88f, center = c)
        drawCircle(Color.White.copy(alpha = 0.15f * a), radius = r * 0.05f, center = Offset(c.x - r * 0.3f, c.y - r * 0.3f))
        val pw = size.width * 0.15f; val ph = size.width * 0.18f; val px = c.x - size.width * 0.38f; val py = c.y - ph * 0.5f
        drawRoundRect(Color(0xFF533483).copy(alpha = 0.9f), Offset(px, py), Size(pw * 1.1f, ph), CornerRadius(3f, 3f))
        drawRoundRect(Color.White, Offset(px + 1f, py + 1f), Size(pw * 1.1f - 2f, ph - 2f), CornerRadius(2f, 2f))
        val tx = px + pw * 1.3f
        for (i in 0..3) { val lw = (0.6f - i * 0.1f) * size.width; drawRect(Color.White.copy(alpha = 0.65f - i * 0.1f), Offset(tx, py + i * (ph * 0.22f)), Size(lw, 2.5f)) }
        val imgY = py + ph * 1.2f
        drawRoundRect(Color(0xFF533483).copy(alpha = 0.7f), Offset(px, imgY), Size(pw * 1.1f, ph * 0.8f), CornerRadius(3f, 3f))
        drawRoundRect(Color.White, Offset(px + 1f, imgY + 1f), Size(pw * 1.1f - 2f, ph * 0.8f - 2f), CornerRadius(2f, 2f))
        val tx2 = px + pw * 1.3f
        for (i in 0..2) { drawRect(Color.White.copy(alpha = 0.45f), Offset(tx2, imgY + i * (ph * 0.25f)), Size(pw * 1.8f, 2.5f)) }
    }
}

@Composable
fun GlassProfileIcon(selected: Boolean, modifier: Modifier = Modifier) {
    val a by animateFloatAsState(if (selected) 1f else 0.3f, tween(400), label = "g")
    Canvas(modifier = modifier.size(28.dp)) {
        val c = center; val r = size.minDimension / 2
        drawCircle(Brush.radialGradient(listOf(Color(0xFF1A1A2E), Color(0xFF0F3460)), center = c, radius = r), radius = r, center = c)
        if (selected) drawCircle(Color(0xFFE94560).copy(alpha = 0.2f), radius = r * 0.9f, center = c)
        drawCircle(Color.White.copy(alpha = 0.15f * a), radius = r * 0.06f, center = Offset(c.x - r * 0.25f, c.y - r * 0.25f))
        val hr = size.width * 0.14f
        drawCircle(Color.White.copy(alpha = 0.85f), hr, Offset(c.x, c.y - size.width * 0.1f))
        if (selected) drawCircle(Color(0xFFE94560).copy(alpha = 0.4f), hr + 2f, Offset(c.x, c.y - size.width * 0.1f), style = Stroke(1.5f))
        val bp = Path().apply {
            moveTo(c.x - size.width * 0.3f, c.y + size.width * 0.15f)
            quadraticBezierTo(c.x, c.y + size.width * 0.35f, c.x + size.width * 0.3f, c.y + size.width * 0.15f)
            quadraticBezierTo(c.x + size.width * 0.15f, c.y + size.width * 0.2f, c.x, c.y + size.width * 0.18f)
            quadraticBezierTo(c.x - size.width * 0.15f, c.y + size.width * 0.2f, c.x - size.width * 0.3f, c.y + size.width * 0.15f)
            close()
        }
        drawPath(bp, Color.White.copy(alpha = 0.75f))
        if (selected) drawPath(bp, Color(0xFFE94560).copy(alpha = 0.4f), style = Stroke(1.2f))
    }
}
