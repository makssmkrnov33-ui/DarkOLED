package com.darkoled.app.model

object AnimeAvatar {
    fun getAvatarUrl(seed: String): String {
        val safe = java.net.URLEncoder.encode(seed.replace(" ", "").take(20), "UTF-8")
        return "https://api.dicebear.com/9.x/adventurer/png?seed=$safe&size=128&backgroundColor=transparent"
    }
}
