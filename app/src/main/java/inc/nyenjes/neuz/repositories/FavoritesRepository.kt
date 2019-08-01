package inc.nyenjes.neuz.repositories

import inc.nyenjes.neuz.models.NewsArticle
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.rowParser
import org.jetbrains.anko.info

private val logger: AnkoLogger = AnkoLogger<String>()

class FavoritesRepository(private val newsDbOpenHelper: NewsDbOpenHelper) {
private val TABLE_NAME = "favorites"

    fun addFavorites(newsResult: NewsArticle): Long {
        var ss = newsResult
        var response = newsDbOpenHelper.use {
            insert(TABLE_NAME,
                "source" to "newsResult.source",
                "author" to newsResult.author,
                "title" to newsResult.title,
                "description" to newsResult.description,
                "url" to newsResult.url,
                "urlToImage" to newsResult.urlToImage,
                "publishedAt" to "newsResult.publishedAt",
                "content" to newsResult.content

                )
        }
        return response
    }

    fun removeFavorite(newsTitle : String) {
        newsDbOpenHelper.use {
            delete(TABLE_NAME,
                "title = {newsTitle}",
                newsTitle to newsTitle
                )
        }
    }

    fun isFavorite(newsTitle: String?): Boolean {
        var favorites = getAllFavorites()

        return favorites!!.any { news ->
            newsTitle == newsTitle
        }
    }

    fun getAllFavorites(): ArrayList<NewsArticle>? {
        var newsItems = ArrayList<NewsArticle>()

        val newsRowParser = rowParser{
                author: String,
                title: String,
                description: String,
                url: String,
                urlToImage: String,
                content: String ->
            val article = NewsArticle()

            article.author = author
            article.title = title
            article.description = description
            article.url = url
            article.urlToImage = urlToImage
            article.content = content
            logger.info { "getAllFavorites : ${article}" }
println("--------------article ${article}")
            newsItems.add(article)

        }
        newsItems

        return newsItems
    }
}