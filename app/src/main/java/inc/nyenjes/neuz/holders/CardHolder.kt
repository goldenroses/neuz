package inc.nyenjes.neuz.holders

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import inc.nyenjes.neuz.R
import inc.nyenjes.neuz.activities.NeuzViewActivity
import inc.nyenjes.neuz.models.NewsArticle

class CardHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    val newsTitle: TextView = itemView.findViewById<TextView>(R.id.newsTitle)
    val newsIcon: ImageView = itemView.findViewById<ImageView>(R.id.newsIcon)
    val newsDescription: TextView = itemView.findViewById<TextView>(R.id.newsDescription)

    var currentNeuzItem: NewsArticle? = null

    init {
        newsTitle.setOnClickListener{
            val intent = Intent(itemView.context, NeuzViewActivity::class.java)
            intent.putExtra("newsArticle", Gson().toJson(currentNeuzItem))
            itemView.context.startActivity(intent)
        }
    }

    fun updateCurrentItem(currentItem: NewsArticle) {
        currentNeuzItem = currentItem
        if(currentNeuzItem!!.urlToImage != null) {
            Picasso.with(itemView.context).load(currentNeuzItem!!.urlToImage).into(newsIcon)
        }
    }

}