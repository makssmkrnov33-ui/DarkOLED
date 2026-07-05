package com.darkoled.app.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object AnimeAvatar {
    var defaultStyle by mutableStateOf("adventurer")

    val styles = listOf(
        "adventurer"         to "Adventurer",
        "adventurer-neutral" to "Adventurer Neutral",
        "avataaars"          to "Avataaars",
        "avataaars-neutral"  to "Avataaars Neutral",
        "big-smile"          to "Big Smile",
        "bottts"             to "Bottts",
        "bottts-neutral"     to "Bottts Neutral",
        "croodles"           to "Croodles",
        "croodles-neutral"   to "Croodles Neutral",
        "fun-emoji"          to "Fun Emoji",
        "icons"              to "Icons",
        "identicon"          to "Identicon",
        "initials"           to "Initials",
        "lorelei"            to "Lorelei",
        "lorelei-neutral"    to "Lorelei Neutral",
        "micah"              to "Micah",
        "miniavs"            to "Miniavs",
        "notionists"         to "Notionists",
        "notionists-neutral" to "Notionists Neutral",
        "open-peeps"         to "Open Peeps",
        "personas"           to "Personas",
        "pixel-art"          to "Pixel Art",
        "pixel-art-neutral"  to "Pixel Art Neutral",
        "rings"              to "Rings",
        "shapes"             to "Shapes",
        "thumbs"             to "Thumbs"
    )

    fun getAvatarUrl(seed: String, style: String = defaultStyle): String {
        val safe = java.net.URLEncoder.encode(seed.replace(" ", "").take(20), "UTF-8")
        return "https://api.dicebear.com/9.x/$style/png?seed=$safe&size=128&backgroundColor=transparent"
    }
}
