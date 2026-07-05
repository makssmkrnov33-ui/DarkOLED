@Entity(tableName = "saved_articles")
data class SavedArticle(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val imageUrl: String,
    val savedAt: Long
)

@Dao
interface ArticleDao {
    @Query("SELECT * FROM saved_articles ORDER BY savedAt DESC")
    fun getAllSaved(): Flow<List<SavedArticle>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(article: SavedArticle)
}