package com.darkoled.app.ui.voice

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkoled.app.engine.MessengerManager

@Composable
fun VoiceAssistantScreen(modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    val voiceAI = remember { MessengerManager.voiceAI }
    val isSpeaking by voiceAI.isSpeaking.collectAsState()
    val isLearning by voiceAI.isLearning.collectAsState()
    val isListening by voiceAI.isListeningFlow.collectAsState()
    val transcript by voiceAI.transcript.collectAsState()
    val status by voiceAI.status.collectAsState()

    DisposableEffect(Unit) {
        voiceAI.startContinuousListening()
        onDispose { voiceAI.stopContinuousListening() }
    }

    val bgGradient = listOf(
        Color(0xFF1A1A2E),
        if (isLearning) Color(0xFF2D1B69) else if (isSpeaking) Color(0xFF1B3A4D) else Color(0xFF16213E)
    )

    Column(
        modifier = modifier.fillMaxSize().background(Brush.verticalGradient(bgGradient)).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))

        Text("Голосовой AI", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.White)
        Spacer(Modifier.height(4.dp))
        Text("Ассистентка 18 лет", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
        Spacer(Modifier.height(8.dp))

        // Status badge
        val statusColor by animateColorAsState(
            targetValue = when {
                isLearning -> Color(0xFFFF69B4)
                isSpeaking -> Color(0xFF00FF88)
                else -> Color.White.copy(alpha = 0.5f)
            },
            label = "statusColor"
        )
        Text(status, color = statusColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)

        Spacer(Modifier.weight(0.3f))

        // Animated avatar circle
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(200.dp)) {
                val c = center
                val r = size.minDimension / 2
                drawCircle(Color(0xFFFF69B4).copy(alpha = 0.08f), radius = r * 1.15f, center = c)
                drawCircle(Color(0xFFFF69B4).copy(alpha = 0.12f), radius = r * 0.95f, center = c,
                    style = Stroke(width = 2f))
                val pulseR = r + (kotlin.math.sin(System.currentTimeMillis() / 500.0) * 8).toFloat()
                if (isSpeaking || isLearning) {
                    drawCircle(Color(0xFFFF69B4).copy(alpha = if (isSpeaking) 0.25f else 0.15f),
                        radius = pulseR, center = c, style = Stroke(width = 3f))
                }
                drawCircle(Color(0xFFFF69B4).copy(alpha = 0.15f), radius = r, center = c)
                drawCircle(Color(0xFFFFB6C1), radius = r * 0.7f, center = c)
                val eyeSize = r * 0.13f
                drawCircle(Color(0xFF333333), radius = eyeSize, center = Offset(c.x - r * 0.2f, c.y - r * 0.05f))
                drawCircle(Color(0xFF333333), radius = eyeSize, center = Offset(c.x + r * 0.2f, c.y - r * 0.05f))
                val eyeShine = eyeSize * 0.4f
                drawCircle(Color.White, radius = eyeShine, center = Offset(c.x - r * 0.16f, c.y - r * 0.1f))
                drawCircle(Color.White, radius = eyeShine, center = Offset(c.x + r * 0.24f, c.y - r * 0.1f))
                val blush = r * 0.12f
                drawCircle(Color(0xFFFFB6C1).copy(alpha = 0.5f), radius = blush, center = Offset(c.x - r * 0.35f, c.y + r * 0.1f))
                drawCircle(Color(0xFFFFB6C1).copy(alpha = 0.5f), radius = blush, center = Offset(c.x + r * 0.35f, c.y + r * 0.1f))
                val mouthPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(c.x - r * 0.1f, c.y + r * 0.18f)
                    quadraticTo(c.x, c.y + r * 0.28f, c.x + r * 0.1f, c.y + r * 0.18f)
                }
                drawPath(mouthPath, Color(0xFFFF69B4), style = Stroke(2.5f))
            }

            // Status indicator ring
            if (isListening) {
                Canvas(modifier = Modifier.size(220.dp)) {
                    val sweep = (System.currentTimeMillis() % 2000) / 2000f * 360f
                    drawArc(Color(0xFFFF69B4).copy(alpha = 0.5f), sweep, 60f, false,
                        style = Stroke(2f), size = androidx.compose.ui.geometry.Size(size.width, size.height))
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Transcript
        if (transcript.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f))
            ) {
                Text(transcript, modifier = Modifier.padding(16.dp),
                    color = Color.White, fontSize = 16.sp, textAlign = TextAlign.Center)
            }
        }

        Spacer(Modifier.weight(0.5f))

        // Learning toggle button
        val learnSrc = remember { MutableInteractionSource() }
        val learnP by learnSrc.collectIsPressedAsState()
        val learnScale by animateFloatAsState(if (learnP) 0.85f else 1f, spring(0.5f, 500f), label = "l")
        Box(
            modifier = Modifier
                .size(64.dp).scale(learnScale)
                .clip(CircleShape)
                .background(if (isLearning) Color(0xFFFF69B4) else Color.White.copy(alpha = 0.12f))
                .clickable(learnSrc, null) { voiceAI.toggleLearning() },
            contentAlignment = Alignment.Center
        ) {
            Text(if (isLearning) "🎓" else "📚", fontSize = 28.sp)
        }
        Spacer(Modifier.height(8.dp))
        Text(if (isLearning) "Обучение включено" else "Нажми для обучения",
            color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)

        Spacer(Modifier.height(16.dp))

        // Status info
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusDot("Слушает", isListening = true)
            StatusDot("Говорит", isListening = isSpeaking)
            StatusDot("Учится", isListening = isLearning)
        }

        Spacer(Modifier.height(16.dp))

        Text("🎀 Голос: девушка, 18 лет", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
    }
}

@Composable
private fun StatusDot(label: String, isListening: Boolean) {
    val dotColor by animateColorAsState(
        targetValue = if (isListening) Color(0xFF00FF88) else Color.White.copy(alpha = 0.3f),
        label = "dot"
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(dotColor))
        Spacer(Modifier.size(4.dp))
        Text(label, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
    }
}
