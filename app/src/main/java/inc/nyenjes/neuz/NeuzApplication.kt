package inc.nyenjes.neuz

import android.app.Application
import inc.nyenjes.neuz.managers.NeuzDataProvider
import inc.nyenjes.neuz.managers.NeusDataManager
import inc.nyenjes.neuz.repositories.FavoritesRepository
import inc.nyenjes.neuz.repositories.HistoryRepository
import inc.nyenjes.neuz.repositories.NewsDbOpenHelper

class NeuzApplication: Application() {
    var dbHelper: NewsDbOpenHelper? = null
    var favoritesRepository: FavoritesRepository? = null
    var historyRepository: HistoryRepository? = null
    var neuzDataProvider: NeuzDataProvider? = null
    var neuzDataManager: NeusDataManager? = null
        private set


    override fun onCreate() {
        super.onCreate()

        dbHelper = NewsDbOpenHelper(applicationContext)

        favoritesRepository = FavoritesRepository(dbHelper!!)
        historyRepository = HistoryRepository(dbHelper!!)

        neuzDataProvider = NeuzDataProvider()
        neuzDataManager = NeusDataManager(neuzDataProvider!!, favoritesRepository!!, historyRepository!!)
    }

}
