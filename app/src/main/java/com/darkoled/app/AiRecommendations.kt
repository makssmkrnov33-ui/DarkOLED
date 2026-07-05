package com.darkoled.app

import com.darkoled.app.model.Article
import com.darkoled.app.model.UserPreferences

object AiRecommendations {
    fun recommend(articles: List<Article>, preferences: UserPreferences): List<Article> {
        if (preferences.interests.isEmpty()) return articles.take(10)
        return articles.filter { article ->
            preferences.interests.any { keyword ->
                article.title.contains(keyword, ignoreCase = true) ||
                article.content.contains(keyword, ignoreCase = true)
            }
        }
    }
}
