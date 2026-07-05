package com.darkoled.app.ui.news

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darkoled.app.model.Article

@Composable
fun NewsFeedScreen(modifier: Modifier = Modifier, onArticleClick: (Article) -> Unit = {}) {
    val articles = remember { mockArticles() }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("News", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(12.dp))
        LazyColumn {
            items(articles) { article ->
                NewsCardWithLike(article = article, onClick = { onArticleClick(article) })
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

private fun mockArticles() = listOf(
    Article("1", "Breaking News", "Content here...", author = "John", publishedAt = System.currentTimeMillis()),
    Article("2", "Tech Update", "More content...", author = "Jane", publishedAt = System.currentTimeMillis() - 7200000)
)
