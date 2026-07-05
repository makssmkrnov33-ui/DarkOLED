data class Chat(
    val id: String = "",
    val name: String = "",
    val isGroup: Boolean = false,
    val members: List<String> = emptyList(),  // UID участников
    val avatarUrl: String = "",
    val lastMessage: String = "",
    val lastMessageTime: Long = 0L
)

data class Message(
    val id: String = "",
    val senderId: String = "",
    val type: MessageType = MessageType.TEXT,  // TEXT, IMAGE, VIDEO, VOICE, FILE
    val text: String = "",
    val mediaUrl: String = "",
    val waveform: List<Int> = emptyList(),     // Для голосовых (амплитуды)
    val durationMs: Long = 0L,                 // Длительность голосового
    val fileName: String = "",
    val fileSize: Long = 0L,
    val timestamp: Long = System.currentTimeMillis(),
    val isEncrypted: Boolean = true            // Иконка замка
)

enum class MessageType { TEXT, IMAGE, VIDEO, VOICE, FILE }