package inc.nyenjes.neuz.managers

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.google.gson.Gson
import inc.nyenjes.neuz.activities.HomeActivity
import inc.nyenjes.neuz.activities.NoInternetActivity
import inc.nyenjes.neuz.models.NewsResults
import inc.nyenjes.neuz.models.URLs
import java.io.Reader

class NeuzDataProvider: AppCompatActivity() {
    private val X_API_KEY = "11841d1d935e4fdca761feae5478a401"
    class NewsDeserializer: ResponseDeserializable<NewsResults> {
        override fun deserialize(reader: Reader): NewsResults? = Gson().fromJson(reader, NewsResults::class.java)
    }

    init {
        FuelManager.instance.baseHeaders = mapOf("User-Agent" to "Nyenjes")
    }

    fun getTopArticlesForTheDay(responseHandler: (result: NewsResults) -> Unit?) {
        URLs.getTopNewsArticlesUrl().httpGet().header("x-api-key", X_API_KEY).responseObject(NewsDeserializer()){
                _,response, result ->
            if(response.statusCode != 200 || response == null) {
                response
                var intent = Intent(this, NoInternetActivity::class.java)
                startActivity(intent)
                throw Exception("Unable to get news content")
            }
            val(data,_) = result
            responseHandler.invoke(data as NewsResults)
        }

    }
}