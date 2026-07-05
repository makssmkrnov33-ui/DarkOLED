package com.darkoled.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_articles")
data class ArticleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val imageUrl: String? = null,
    val savedAt: Long = System.currentTimeMillis()
)
