package com.darkoled.app.model

data class Article(
    val id: String,
    val title: String,
    val content: String,
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val author: String = "",
    val authorAvatar: String = "",
    val publishedAt: Long = 0L,
    val likes: Int = 0,
    val commentCount: Int = 0,
    val shareCount: Int = 0,
    val isStarred: Boolean = false
)
