package com.darkoled.app.model

enum class MessageType { TEXT, IMAGE, VIDEO, VOICE, FILE }

data class Message(
    val id: String,
    val chatId: String,
    val senderId: String,
    val text: String = "",
    val type: MessageType = MessageType.TEXT,
    val timestamp: Long = 0L,
    val mediaUrl: String? = null,
    val waveform: List<Int>? = null,
    val fileName: String? = null,
    val fileSize: Long? = null,
    val isEncrypted: Boolean = false
)
