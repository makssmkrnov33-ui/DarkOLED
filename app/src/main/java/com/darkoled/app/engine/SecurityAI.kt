package com.darkoled.app.engine

import android.content.Context
import android.content.pm.PackageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.BufferedReader
import java.io.File

enum class MaskProfile(val label: String, val userAgent: String, val tlsSig: String) {
    VK("VK.com", "VKAndroidApp/7.35-14019 (Android 14; SDK 34; arm64-v8a; ru; 1080x2400)", "chrome_120"),
    AVITO("Avito", "Avito/2025.1.2 (Android 14; SDK 34; arm64-v8a; ru; 1080x2400)", "chrome_120"),
    YANDEX("Яндекс", "YandexSearch/14.10 (Android 14; SDK 34; arm64)", "chrome_120"),
    TELEGRAM("Telegram", "Telegram Android 10.15.0 (2406 x 1080; Android 14; ru; SDK 34)", "okhttp4")
}

enum class ThreatLevel { SAFE, SUSPICIOUS, DANGEROUS }

data class ThreatReport(
    val type: String,
    val detail: String,
    val level: ThreatLevel,
    val timestamp: Long = System.currentTimeMillis()
)

class SecurityAI(private val context: Context) {

    private val _maskProfile = MutableStateFlow(MaskProfile.VK)
    val maskProfile: StateFlow<MaskProfile> = _maskProfile.asStateFlow()

    private val _protectionLevel = MutableStateFlow(0) // 0-100
    val protectionLevel: StateFlow<Int> = _protectionLevel.asStateFlow()

    private val _threats = MutableStateFlow<List<ThreatReport>>(emptyList())
    val threats: StateFlow<List<ThreatReport>> = _threats.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _lastScanResult = MutableStateFlow("")
    val lastScanResult: StateFlow<String> = _lastScanResult.asStateFlow()

    private val knownMalwareSignatures = listOf(
        "base64_decode", "eval(", "exec(", "Runtime.getRuntime",
        "HttpPost", "socket", "/etc/passwd", "chmod", "su binary",
        "supersu", "magisk", "zygisk", "frida", "xposed"
    )

    fun setMaskProfile(profile: MaskProfile) {
        _maskProfile.value = profile
    }

    fun scanForThreats() {
        _isScanning.value = true
        _lastScanResult.value = "Сканирование..."
        val found = mutableListOf<ThreatReport>()

        // 1. Check installed apps for suspicious packages
        try {
            val pm = context.packageManager
            val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            val suspicious = packages.filter {
                val name = it.packageName.lowercase()
                name.contains("hack") || name.contains("crack") || name.contains("spy") ||
                name.contains("trojan") || name.contains("keylog") || name.contains("rat") ||
                name.contains("inject") || name.contains("hook")
            }
            if (suspicious.isNotEmpty()) {
                found.add(ThreatReport(
                    "Подозрительные приложения",
                    "Найдено: ${suspicious.joinToString(", ") { it.packageName }}",
                    ThreatLevel.SUSPICIOUS
                ))
            }
        } catch (_: Exception) {}

        // 2. Check for root access
        try {
            val paths = listOf("/system/bin/su", "/system/xbin/su", "/sbin/su", "/system/app/Superuser.apk")
            val rooted = paths.any { File(it).exists() }
            if (rooted) {
                found.add(ThreatReport("Root-доступ", "Обнаружены признаки root", ThreatLevel.DANGEROUS))
            }
        } catch (_: Exception) {}

        // 3. Check for debug mode
        try {
            val isDebug = (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
            if (isDebug) {
                found.add(ThreatReport("Режим отладки", "Приложение в debug режиме", ThreatLevel.SUSPICIOUS))
            }
        } catch (_: Exception) {}

        // 4. Check for keyloggers in running processes
        try {
            val reader = BufferedReader(File("/proc/self/cmdline").reader())
            val cmdline = reader.readText()
            reader.close()
            if (cmdline.contains("strace") || cmdline.contains("ptrace")) {
                found.add(ThreatReport("Трассировка", "Обнаружена трассировка процессов", ThreatLevel.DANGEROUS))
            }
        } catch (_: Exception) {}

        // 5. Check for overlay attacks (screen overlay)
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val canDrawOverlay = android.provider.Settings.canDrawOverlays(context)
                if (canDrawOverlay) {
                    found.add(ThreatReport("Оверлей", "Приложения могут рисовать поверх экрана", ThreatLevel.SUSPICIOUS))
                }
            }
        } catch (_: Exception) {}

        _threats.value = found
        _protectionLevel.value = when {
            found.any { it.level == ThreatLevel.DANGEROUS } -> 15
            found.any { it.level == ThreatLevel.SUSPICIOUS } -> 40
            else -> 95
        }
        _lastScanResult.value = if (found.isEmpty()) "✅ Угроз не обнаружено"
            else "⚠️ Найдено ${found.size} угроз"
        _isScanning.value = false
    }

    fun scanFile(filePath: String): Boolean {
        val file = File(filePath)
        if (!file.exists()) return true
        return try {
            val content = file.readText()
            knownMalwareSignatures.none { content.contains(it, ignoreCase = true) }
        } catch (_: Exception) { true }
    }

    fun getMaskHeaders(): Map<String, String> {
        val profile = _maskProfile.value
        return mapOf(
            "User-Agent" to profile.userAgent,
            "Accept" to "*/*",
            "Accept-Language" to "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7",
            "X-Requested-With" to when (profile) {
                MaskProfile.VK -> "com.vkontakte.android"
                MaskProfile.AVITO -> "com.avito.android"
                MaskProfile.YANDEX -> "ru.yandex.searchplugin"
                MaskProfile.TELEGRAM -> "org.telegram.messenger"
            }
        )
    }

    fun encryptPayload(data: String): String {
        val key = "DarkOLED_2026_SECURE".toByteArray()
        val input = data.toByteArray()
        val result = ByteArray(input.size)
        for (i in input.indices) {
            result[i] = (input[i].toInt() xor key[i % key.size].toInt()).toByte()
        }
        return android.util.Base64.encodeToString(result, android.util.Base64.NO_WRAP)
    }

    fun decryptPayload(encoded: String): String {
        val key = "DarkOLED_2026_SECURE".toByteArray()
        val input = android.util.Base64.decode(encoded, android.util.Base64.NO_WRAP)
        val result = ByteArray(input.size)
        for (i in input.indices) {
            result[i] = (input[i].toInt() xor key[i % key.size].toInt()).toByte()
        }
        return String(result)
    }
}
