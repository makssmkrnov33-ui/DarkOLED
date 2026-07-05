package com.darkoled.app.model

data class Chat(
    val id: String,
    val name: String,
    val isGroup: Boolean = false,
    val members: List<String> = emptyList(),
    val avatarUrl: String? = null,
    val lastMessage: String = "",
    val lastMessageTime: Long = 0L
)
