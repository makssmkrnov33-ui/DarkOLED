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
        try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val doc = builder.parse(StringReader(xml))
            val nodeList = doc.getElementsByTagName("item")

            for (i in 0 until nodeList.length) {
                val item = nodeList.item(i)
                val childNodes = item.childNodes
                var title = ""
                var description = ""
                var imageUrl: String? = null
                var link = ""

                for (j in 0 until childNodes.length) {
                    val node = childNodes.item(j)
                    when (node.nodeName) {
                        "title" -> title = node.textContent ?: ""
                        "description" -> description = node.textContent ?: ""
                        "link" -> link = node.textContent ?: ""
                        "enclosure" -> {
                            val urlAttr = node.attributes?.getNamedItem("url")
                            imageUrl = urlAttr?.textContent
                        }
                    }
                }

                articles.add(Article(
                    id = link.ifEmpty { "article_$i" },
                    title = title.ifEmpty { "Untitled" },
                    content = description,
                    imageUrl = imageUrl,
                    author = "TACC",
                    publishedAt = System.currentTimeMillis() - i * 60000L
                ))
            }
        } catch (_: Exception) {}
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
