package com.darkoled.app.model

data class Article(
    val id: String,
    val title: String,
    val content: String = "",
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

data class ShortVideo(
    val id: String,
    val title: String,
    val videoUrl: String,
    val author: String,
    val likes: Int = 0,
    val comments: Int = 0,
    val shares: Int = 0,
    val isLiked: Boolean = false,
    val isStarred: Boolean = false
)
