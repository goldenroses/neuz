package inc.nyenjes.neuz.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import inc.nyenjes.neuz.R
import inc.nyenjes.neuz.holders.CardHolder
import inc.nyenjes.neuz.models.NewsArticle

class CardAdapter(): RecyclerView.Adapter<CardHolder>() {

    val currentResults: ArrayList<NewsArticle> = ArrayList<NewsArticle>()

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): CardHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return CardHolder(itemView)
    }
    override fun getItemCount(): Int = currentResults.size

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        val currentItem = currentResults[position]
        holder.newsTitle.text = currentItem.title
        holder.newsDescription.text = currentItem.description
        holder.updateCurrentItem(currentItem)

    }
}
