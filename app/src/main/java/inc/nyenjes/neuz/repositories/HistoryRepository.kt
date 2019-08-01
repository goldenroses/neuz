package inc.nyenjes.neuz.repositories

import com.google.gson.Gson
import inc.nyenjes.neuz.models.NewsArticle
import inc.nyenjes.neuz.models.Profile
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.rowParser
import java.time.Instant
import java.util.*
import kotlin.collections.ArrayList

class HistoryRepository(private val newsDbOpenHelper: NewsDbOpenHelper) {
private val TABLE_NAME = "history"

    fun addHistory(newsResult: NewsArticle) {
        newsDbOpenHelper.use {
            insert(TABLE_NAME,
                "source" to Gson().toJson(newsResult.source),
                "author" to newsResult.author,
                "title" to newsResult.title,
                "description" to newsResult.description,
                "url" to newsResult.url,
                "urlToImage" to newsResult.urlToImage,
                "publishedAt" to newsResult.publishedAt,
                "content" to newsResult.content

            )
        }
    }

    fun removeHistory(newsTitle : String) {
        newsDbOpenHelper.use {
            delete(TABLE_NAME,
                "title = {newsTitle}",
                newsTitle to newsTitle
            )
        }
    }

    fun getAllHistory(): ArrayList<NewsArticle> {
        var newsItems = ArrayList<NewsArticle>()

        val newsRowParser = rowParser{
                source: Profile,
                author: String,
                title: String,
                description: String,
                url: String,
                urlToImage: String,
                publishedAt: Date,
                content: String ->
            var article = NewsArticle()

            article.source = source
            article.author = author
            article.title = title
            article.description = description
            article.url = url
            article.urlToImage = urlToImage
            article.publishedAt = publishedAt
            article.content = content

            newsItems.add(article)

        }

        return newsItems
    }
}