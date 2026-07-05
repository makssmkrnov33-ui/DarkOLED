package com.darkoled.app.model

object AnimeAvatar {
    fun getAvatarUrl(seed: String): String {
        val safe = seed.replace(" ", "").take(20)
        return "https://api.dicebear.com/9.x/adventurer/svg?seed=$safe"
    }
}
