package inc.nyenjes.neuz.models

object URLs {

    val baseUrl = "http://newsapi.org/v2"

    fun getSearchUrl(query: String?, limit: Int?, country: String? = "us") : String {
        return baseUrl + "/top-headlines?country=${country}"
    }

    fun getTopNewsArticlesUrl(country: String? = "us") : String {
        val base = baseUrl + "/top-headlines?country=${country}"

        return  base
    }
}
