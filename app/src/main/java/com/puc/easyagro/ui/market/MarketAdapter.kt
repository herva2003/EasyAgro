package com.puc.easyagro.ui.market

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.puc.easyagro.R

class MarketAdapter(private var marketList: List<Market>, private val onItemClickListener: (String, String) -> Unit) :
    RecyclerView.Adapter<MarketAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemMarketTextView: TextView = itemView.findViewById(R.id.text_view_item_market)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_market, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemMarket = marketList[position]
        holder.itemMarketTextView.text = itemMarket.nome

        holder.itemView.setOnClickListener {
            itemMarket._id?.let { id ->
                itemMarket.nome?.let { it1 -> onItemClickListener(id, it1) }
            }
        }

    }

    override fun getItemCount(): Int {
        return marketList.size
    }

    fun updateData(newCulturasList: List<Market>) {
        marketList = newCulturasList
        notifyDataSetChanged()
    }
}