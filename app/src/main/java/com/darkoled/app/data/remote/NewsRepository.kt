package com.darkoled.app.data.remote

import com.darkoled.app.model.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

object NewsRepository {
    private const val RSS_URL = "https://tass.ru/rss/v2.xml"

    suspend fun fetchNews(): List<Article> = withContext(Dispatchers.IO) {
        try {
            val url = java.net.URL(RSS_URL)
            val xml = url.readText()
            parseRssSimple(xml)
        } catch (e: Exception) {
            fallbackNews()
        }
    }

    private fun parseRssSimple(xml: String): List<Article> {
        val articles = mutableListOf<Article>()
        val items = xml.split("<item>").drop(1)
        for (item in items) {
            val title = extractTag(item, "title")
            val desc = extractTag(item, "description")
            val link = extractTag(item, "link")
            val imgUrl = extractEnclosure(item)
            if (title != null) {
                articles.add(Article(
                    id = link ?: "a_${articles.size}",
                    title = title,
                    content = desc ?: "",
                    imageUrl = imgUrl,
                    author = "TACC",
                    publishedAt = System.currentTimeMillis() - articles.size * 60000L
                ))
            }
        }
        return articles
    }

    private fun extractTag(xml: String, tag: String): String? {
        val open = "<$tag>"
        val close = "</$tag>"
        val s = xml.indexOf(open)
        if (s == -1) return null
        val e = xml.indexOf(close, s)
        if (e == -1) return null
        val raw = xml.substring(s + open.length, e)
        return raw.replace("<![CDATA[", "").replace("]]>", "").trim()
    }

    private fun extractEnclosure(xml: String): String? {
        val marker = "<enclosure"
        val s = xml.indexOf(marker)
        if (s == -1) return null
        val e = xml.indexOf("/>", s)
        if (e == -1) return null
        val tag = xml.substring(s, e + 2)
        val urlMarker = "url=\""
        val us = tag.indexOf(urlMarker)
        if (us == -1) return null
        val ue = tag.indexOf("\"", us + urlMarker.length)
        return if (ue == -1) null else tag.substring(us + urlMarker.length, ue)
    }

    private fun fallbackNews(): List<Article> = listOf(
        Article("1", "Cyberpunk 2077: Phantom Liberty — новый трейлер",
            "CD Projekt RED показала геймплей дополнения с Киану Ривзом",
            videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            author = "GameNews", publishedAt = System.currentTimeMillis(),
            likes = 12453, commentCount = 892, shareCount = 3451,
            isStarred = true),
        Article("2", "Аниме-фестиваль 2026 в Токио",
            "Косплей, премьеры и встречи с сэйю — всё самое яркое с AnimeJapan",
            videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
            author = "AnimeWorld", publishedAt = System.currentTimeMillis() - 3600000,
            likes = 8721, commentCount = 456, shareCount = 1203),
        Article("3", "OLED-дисплеи: новая эра мобильных устройств",
            "Тонкие, яркие, энергоэффективные — будущее уже здесь",
            imageUrl = "https://picsum.photos/seed/oled/800/1200",
            author = "TechReview", publishedAt = System.currentTimeMillis() - 7200000,
            likes = 5432, commentCount = 234, shareCount = 876),
        Article("4", "Kawaii-стиль возвращается в моду",
            "Пастельные тона, рюши и аниме-принты снова на подиумах",
            videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            author = "FashionTokyo", publishedAt = System.currentTimeMillis() - 10800000,
            likes = 3210, commentCount = 189, shareCount = 654),
        Article("5", "Кибербезопасность 2026: главные угрозы",
            "Как защитить данные в эпоху цифровых рисков",
            imageUrl = "https://picsum.photos/seed/cyber/800/1200",
            author = "SecurityLab", publishedAt = System.currentTimeMillis() - 14400000,
            likes = 2198, commentCount = 156, shareCount = 432),
        Article("6", "VTuber-концерт собрал 2 миллиона зрителей",
            "Виртуальные айдолы устанавливают новые рекорды популярности",
            videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
            author = "AnimeWorld", publishedAt = System.currentTimeMillis() - 18000000,
            likes = 15678, commentCount = 1023, shareCount = 5678,
            isStarred = true),
        Article("7", "Neuralink: первый пациент играет в шахматы силой мысли",
            "Прорыв в интерфейсах мозг-компьютер от Илона Маска",
            imageUrl = "https://picsum.photos/seed/neural/800/1200",
            author = "TechReview", publishedAt = System.currentTimeMillis() - 21600000,
            likes = 9876, commentCount = 789, shareCount = 2345),
        Article("8", "Солана: эфирные пейзажи",
            "Путешествие по самым красивым местам Японии в 4K",
            videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
            author = "TravelJapan", publishedAt = System.currentTimeMillis() - 25200000,
            likes = 7654, commentCount = 567, shareCount = 1890)
    )
}
