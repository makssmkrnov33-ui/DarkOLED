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
        Article("1", "Технологии будущего: ИИ меняет мир",
            "Нейросети и машинное обучение трансформируют каждую отрасль",
            author = "InstaSIM", publishedAt = System.currentTimeMillis()),
        Article("2", "OLED-дисплеи: новая эра",
            "Тонкие, яркие, энергоэффективные — будущее уже здесь",
            author = "InstaSIM", publishedAt = System.currentTimeMillis() - 3600000),
        Article("3", "Кибербезопасность 2026",
            "Как защитить данные в эпоху цифровых угроз",
            author = "InstaSIM", publishedAt = System.currentTimeMillis() - 7200000)
    )
}
