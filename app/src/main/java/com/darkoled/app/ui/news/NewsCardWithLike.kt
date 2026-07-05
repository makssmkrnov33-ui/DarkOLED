package com.darkoled.app.ui.news

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.darkoled.app.model.Article

@Composable
fun NewsCardWithLike(article: Article, onClick: () -> Unit = {}) {
    var likes by remember { mutableIntStateOf(article.likes) }
    var liked by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (liked) 1.05f else 1f)

    Card(
        onClick = onClick,
        modifier = Modifier
            .size(340.dp, 240.dp)
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        liked = true
                        likes++
                    }
                )
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Text(article.title, style = MaterialTheme.typography.headlineMedium)
        Text(article.content, style = MaterialTheme.typography.bodyMedium)
        Text("❤ $likes", style = MaterialTheme.typography.labelMedium)
    }
}
