package com.darkoled.app.model

import androidx.compose.ui.graphics.Color

data class ChatTheme(
    val name: String,
    val backgroundColors: List<Color>,
    val incomingBubbleColor: Color,
    val outgoingBubbleColor: Color,
    val outgoingBorderColor: Color,
    val starColor: Color = Color.Transparent
) {
    companion object {
        val PINK_GRADIENT = ChatTheme(
            name = "Pink Gradient",
            backgroundColors = listOf(Color(0xFFFFF0F5), Color(0xFFFFE4E1)),
            incomingBubbleColor = Color(0xFFFFB6C1),
            outgoingBubbleColor = Color.White,
            outgoingBorderColor = Color(0xFFFFB6C1),
            starColor = Color(0xFFFFD700)
        )

        val MINT_GREEN = ChatTheme(
            name = "Mint Green",
            backgroundColors = listOf(Color(0xFFF0FFF0), Color(0xFFE0F8E0)),
            incomingBubbleColor = Color(0xFF98FB98),
            outgoingBubbleColor = Color.White,
            outgoingBorderColor = Color(0xFF98FB98)
        )

        val TOKYO_NIGHT = ChatTheme(
            name = "Tokyo Night",
            backgroundColors = listOf(Color(0xFF1A1B2E), Color(0xFF2D2E4A)),
            incomingBubbleColor = Color(0xFF5658A8),
            outgoingBubbleColor = Color(0xFF2D2E4A),
            outgoingBorderColor = Color(0xFF5658A8),
            starColor = Color(0xFFFFD700)
        )

        val Y2K_PATTERN = ChatTheme(
            name = "Y2K Pattern",
            backgroundColors = listOf(Color(0xFFE6E6FA), Color(0xFFFFE4E1)),
            incomingBubbleColor = Color(0xFFFF69B4),
            outgoingBubbleColor = Color.White,
            outgoingBorderColor = Color(0xFFFF69B4),
            starColor = Color(0xFFFFD700)
        )

        val themes = listOf(PINK_GRADIENT, MINT_GREEN, TOKYO_NIGHT, Y2K_PATTERN)
    }
}

enum class AccentScheme(val label: String, val color: Color) {
    ELECTRIC_BLUE("Electric Blue", Color(0xFF0084FF)),
    NEON_LIME("Neon Lime", Color(0xFF00E676)),
    HOT_PINK("Hot Pink", Color(0xFFFF4081))
}
