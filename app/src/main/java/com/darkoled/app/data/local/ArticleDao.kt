package com.darkoled.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Query("SELECT * FROM saved_articles ORDER BY savedAt DESC")
    fun getAllSaved(): Flow<List<ArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(article: ArticleEntity)

    @Query("DELETE FROM saved_articles WHERE id = :id")
    suspend fun delete(id: String)
}
