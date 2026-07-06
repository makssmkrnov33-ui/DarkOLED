package com.darkoled.app.ui.security

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkoled.app.engine.MaskProfile
import com.darkoled.app.engine.MessengerManager
import com.darkoled.app.engine.ThreatLevel

@Composable
fun SecurityScreen(modifier: Modifier = Modifier, onBack: () -> Unit = {}) {
    val security = remember { MessengerManager.securityAI }
    val maskProfile by security.maskProfile.collectAsState()
    val protectionLevel by security.protectionLevel.collectAsState()
    val threats by security.threats.collectAsState()
    val isScanning by security.isScanning.collectAsState()
    val lastResult by security.lastScanResult.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F1E))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) { Text("←", color = Color.White, fontSize = 18.sp) }
            Spacer(Modifier.width(12.dp))
            Text("Безопасность", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.White)
        }
        Spacer(Modifier.height(4.dp))
        Text("Защита от внешних атак и вредоносного ПО",
            color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)

        Spacer(Modifier.height(20.dp))

        // Protection level ring
        Box(
            modifier = Modifier.fillMaxWidth().height(140.dp),
            contentAlignment = Alignment.Center
        ) {
            val levelColor by animateColorAsState(
                targetValue = when {
                    protectionLevel >= 80 -> Color(0xFF00FF88)
                    protectionLevel >= 40 -> Color(0xFFFFD700)
                    else -> Color(0xFFFF4081)
                },
                label = "level"
            )
            Canvas(modifier = Modifier.size(120.dp)) {
                val c = center
                val r = size.minDimension / 2
                drawCircle(Color.White.copy(alpha = 0.06f), radius = r)
                drawArc(
                    Color.White.copy(alpha = 0.15f), -90f, 360f, false,
                    style = Stroke(8f), topLeft = Offset(6f, 6f),
                    size = androidx.compose.ui.geometry.Size(r * 2 - 12, r * 2 - 12)
                )
                drawArc(
                    levelColor, -90f, protectionLevel * 3.6f, false,
                    style = Stroke(8f, cap = StrokeCap.Round), topLeft = Offset(6f, 6f),
                    size = androidx.compose.ui.geometry.Size(r * 2 - 12, r * 2 - 12)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$protectionLevel", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("защита", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
            }
        }

        Spacer(Modifier.height(16.dp))

        // Scan button
        val scanBg by animateColorAsState(
            targetValue = if (isScanning) Color(0xFF2D1B69) else Color(0xFFFF69B4),
            label = "scan"
        )
        Box(
            modifier = Modifier.fillMaxWidth().height(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(scanBg)
                .clickable { security.scanForThreats() },
            contentAlignment = Alignment.Center
        ) {
            Text(if (isScanning) "🔄 Сканирование..." else "🛡 Сканировать систему",
                color = Color.White, fontWeight = FontWeight.SemiBold)
        }

        if (lastResult.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(lastResult, color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
        }

        Spacer(Modifier.height(20.dp))

        // Threats list
        if (threats.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Найденные угрозы", fontWeight = FontWeight.SemiBold, color = Color.White)
                    Spacer(Modifier.height(12.dp))
                    threats.forEach { threat ->
                        val dotColor = when (threat.level) {
                            ThreatLevel.SAFE -> Color(0xFF00FF88)
                            ThreatLevel.SUSPICIOUS -> Color(0xFFFFD700)
                            ThreatLevel.DANGEROUS -> Color(0xFFFF4081)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(dotColor))
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(threat.type, color = Color.White, fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium)
                                Text(threat.detail, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // Mask profile
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Маскировка трафика", fontWeight = FontWeight.SemiBold, color = Color.White)
                Spacer(Modifier.height(4.dp))
                Text("Выберите профиль для маскировки сетевых запросов",
                    color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                Spacer(Modifier.height(12.dp))
                MaskProfile.entries.forEach { profile ->
                    val isSel = profile == maskProfile
                    val bgColor by animateColorAsState(
                        targetValue = if (isSel) Color(0xFFFF69B4).copy(alpha = 0.2f) else Color.Transparent,
                        label = "mask"
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(bgColor)
                            .clickable { security.setMaskProfile(profile) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(36.dp).clip(CircleShape)
                                .background(if (isSel) Color(0xFFFF69B4) else Color.White.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(when (profile) {
                                MaskProfile.VK -> "VK"
                                MaskProfile.AVITO -> "Av"
                                MaskProfile.YANDEX -> "Ya"
                                MaskProfile.TELEGRAM -> "Tg"
                            }, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(profile.label, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            Text(profile.userAgent.take(40) + "...",
                                color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp)
                        }
                        Spacer(Modifier.weight(1f))
                        if (isSel) Text("✓", color = Color(0xFFFF69B4), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Info
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Протоколы защиты", fontWeight = FontWeight.SemiBold, color = Color.White)
                Spacer(Modifier.height(10.dp))
                InfoRow("🛡", "XOR-шифрование", "Полезная нагрузка шифруется ключом DarkOLED")
                InfoRow("🎭", "Маскировка User-Agent", "Запросы выглядят как от $maskProfile")
                InfoRow("🔍", "Сканер угроз", "Проверка на root, debug, оверлей, трояны")
                InfoRow("📡", "Мониторинг трафика", "Обнаружение подозрительных соединений")
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun InfoRow(icon: String, title: String, desc: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(icon, fontSize = 18.sp)
        Spacer(Modifier.width(10.dp))
        Column {
            Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(desc, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
        }
    }
}
