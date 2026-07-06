package com.darkoled.app.ui.settings

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class UserPrefs(
    val avatarUri: String = "",
    val nickname: String = "User",
    val notificationMode: String = "all",
    val themeColors: List<String> = emptyList()
)

object SettingsStorage {
    private const val PREFS = "dark_oled_settings"

    fun load(ctx: Context): UserPrefs {
        val p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return UserPrefs(
            avatarUri = p.getString("avatar_uri", "") ?: "",
            nickname = p.getString("nickname", "User") ?: "User",
            notificationMode = p.getString("notif_mode", "all") ?: "all",
            themeColors = (p.getString("theme_colors", "") ?: "").split(",").filter { it.isNotBlank() }
        )
    }

    fun save(ctx: Context, prefs: UserPrefs) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString("avatar_uri", prefs.avatarUri)
            .putString("nickname", prefs.nickname)
            .putString("notif_mode", prefs.notificationMode)
            .putString("theme_colors", prefs.themeColors.joinToString(","))
            .apply()
    }
}
