package com.puc.easyagro.ui.market.pagamento

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.puc.easyagro.R
import com.puc.easyagro.model.Market

class PagamentoAdapter(private var marketList: List<Market>, private val onItemClickListener: (String, String) -> Unit) :
    RecyclerView.Adapter<PagamentoAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val precoItemMarket: TextView = itemView.findViewById(R.id.txt_subtotal_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_market, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val itemMarket = marketList[position]
//        holder.precoItemMarket.text = "R$${itemMarket.price.toString()}"
    }

    override fun getItemCount(): Int {
        return marketList.size
    }

    fun getData(): List<Market> {
        return marketList
    }

    fun updateData(newProdutoList: List<Market>) {
        this.marketList = newProdutoList
        notifyDataSetChanged()
    }
}