package inc.nyenjes.neuz.activities

import android.net.http.SslError
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.google.gson.Gson
import inc.nyenjes.neuz.R
import inc.nyenjes.neuz.managers.NeusDataManager
import inc.nyenjes.neuz.managers.NeuzDataProvider
import inc.nyenjes.neuz.models.NewsArticle
import inc.nyenjes.neuz.repositories.FavoritesRepository
import inc.nyenjes.neuz.repositories.HistoryRepository
import inc.nyenjes.neuz.repositories.NewsDbOpenHelper
import kotlinx.android.synthetic.main.activity_neuz_view.*
import kotlinx.android.synthetic.main.card_item.*
import org.jetbrains.anko.toast
import java.lang.Exception

class NeuzViewActivity : AppCompatActivity() {

    var neuzDataManager: NeusDataManager? = null
    var neuzData: NewsArticle? = NewsArticle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_neuz_view)

        val neuzArticleJson = intent.getStringExtra("newsArticle")
         neuzData = Gson().fromJson<NewsArticle>(neuzArticleJson, NewsArticle::class.java)

        val newsDbOpenHelper = NewsDbOpenHelper(this)
        val neuzDataProvider = NeuzDataProvider()
        val favoritesRepository = FavoritesRepository(newsDbOpenHelper)
        val historyRepository = HistoryRepository(newsDbOpenHelper)

        neuzDataManager = NeusDataManager(neuzDataProvider, favoritesRepository, historyRepository)


        webview.webViewClient = object: WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return true
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                handler?.proceed()
            }
        }

        webview.loadUrl(neuzData!!.url)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
         menuInflater.inflate(R.menu.menu_fave, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        try{
            if(item!!.itemId == android.R.id.home){
                finish()
            }else if(item.itemId == R.id.action_favorite){
                if(!neuzDataManager!!.getIsFavorite(neuzData!!.title)) {
                    toast("Adding to favorite")
                    val response = neuzDataManager!!.addFavorites(neuzData!!)
                }else {
                    toast("removing to favorite")

                    neuzDataManager!!.removeFavorites(neuzData!!.title)
                }

            }

        }catch (ex: Exception) {

        }

        return true
    }
}
