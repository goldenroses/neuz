package inc.nyenjes.neuz.managers

import android.util.Log
import inc.nyenjes.neuz.models.NewsArticle
import inc.nyenjes.neuz.models.NewsResults
import inc.nyenjes.neuz.repositories.FavoritesRepository
import inc.nyenjes.neuz.repositories.HistoryRepository
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDate.parse
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ofPattern
import java.util.*
import kotlin.collections.ArrayList

class NeusDataManager(private val newsDataProvider: NeuzDataProvider,
                      private val favoritesRepository: FavoritesRepository,
                      private val historyRepository: HistoryRepository) {

    private var favoritesCache: ArrayList<NewsArticle>? = null
    private var historyCache: ArrayList<NewsArticle>? = null

    fun getTopArticlesOfTheDay(handler: (result: NewsResults) -> Unit?) {
        newsDataProvider.getTopArticlesForTheDay(handler)
    }

    fun addHistory(newsArticle: NewsArticle) {
        historyCache?.add(newsArticle)
        historyRepository.addHistory(newsArticle)
    }

    fun getHistory(): ArrayList<NewsArticle>? {
        if(historyCache == null)
            historyCache = historyRepository.getAllHistory()
        return historyCache
    }

    fun getFavorites(): ArrayList<NewsArticle>? {
        if(favoritesCache == null)
            favoritesCache = favoritesRepository.getAllFavorites()
        return  favoritesCache
    }

    fun addFavorites(newsArticle: NewsArticle) {
        favoritesCache?.add(newsArticle)
        var response = favoritesRepository.addFavorites(newsArticle)
        Log.e("response","------$response : ")
        println("----------$response")
    }

    fun removeFavorites(newsTitle: String) {
        favoritesCache = favoritesCache!!.filter { it.title != newsTitle } as ArrayList<NewsArticle>
        favoritesRepository.removeFavorite(newsTitle)
    }

    fun getIsFavorite(newsTitle: String): Boolean {
        return favoritesRepository.isFavorite(newsTitle)
    }

    fun clearHistory() {
        historyCache!!.clear()
        val allHistory = historyRepository.getAllHistory()
//        allHistory.forEach{historyRepository.removeHistory(it.title!!)}
    }

}