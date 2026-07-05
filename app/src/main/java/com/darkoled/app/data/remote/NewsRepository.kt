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
            parseRss(xml)
        } catch (e: Exception) {
            fallbackNews()
        }
    }

    private fun parseRss(xml: String): List<Article> {
        val articles = mutableListOf<Article>()
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val doc = builder.parse(StringReader(xml))
        val items = doc.getElementsByTagName("item")

        for (i in 0 until items.length) {
            val item = items.item(i)
            val title = item.childNodes.let { nodes ->
                var t = ""
                for (j in 0 until nodes.length) {
                    if (nodes.item(j).nodeName == "title") t = nodes.item(j).textContent
                }
                t
            } ?: ""

            val description = item.childNodes.let { nodes ->
                var d = ""
                for (j in 0 until nodes.length) {
                    if (nodes.item(j).nodeName == "description") d = nodes.item(j).textContent
                }
                d
            } ?: ""

            var imageUrl: String? = null
            val enclosures = item.childNodes
            for (j in 0 until enclosures.length) {
                if (enclosures.item(j).nodeName == "enclosure") {
                    imageUrl = enclosures.item(j).attributes.getNamedItem("url")?.textContent
                }
            }

            val link = item.childNodes.let { nodes ->
                var l = ""
                for (j in 0 until nodes.length) {
                    if (nodes.item(j).nodeName == "link") l = nodes.item(j).textContent
                }
                l
            } ?: ""

            articles.add(
                Article(
                    id = link,
                    title = title,
                    content = description,
                    imageUrl = imageUrl,
                    author = "TACC",
                    publishedAt = System.currentTimeMillis() - i * 60000L
                )
            )
        }
        return articles
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
