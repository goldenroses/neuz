package inc.nyenjes.neuz.models

import java.time.Instant
import java.util.Date

class NewsResults(
    val status: String? = null,
    val totalResults: Int? = null,
    val articles: Collection<NewsArticle>
)

class NewsArticle (
    var source: Profile? = null,
    var author: String? = null,
    var title: String = "",
    var description: String? = null,
    var url: String? = null,
    var urlToImage: String? = null,
    var publishedAt: Date? = null,
    var content: String? = null
)

class Profile (
    val id: String? = null,
    val name: String? = null
)