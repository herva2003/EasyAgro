package com.puc.easyagro.ui.perfil.buttons.minhasCompras.comprasDetalhes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.puc.easyagro.R
import com.puc.easyagro.model.Order
import com.puc.easyagro.model.Product

class ComprasDetalhesAdapter (private var detalhesList: List<Product>, private val onItemClickListener: (Product) -> Unit) :
    RecyclerView.Adapter<ComprasDetalhesAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val produtoId: TextView = itemView.findViewById(R.id.txt_produto_id)
        val quantity: TextView = itemView.findViewById(R.id.txt_quantity)
        val buyerId: TextView = itemView.findViewById(R.id.txt_buyer_id)
        val sellerId: TextView = itemView.findViewById(R.id.txt_seller_id)
        val price: TextView = itemView.findViewById(R.id.txt_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_detalhe_compras, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemMarket = detalhesList[position]

        holder.produtoId.text = "produtoId: " + itemMarket.productId
        holder.quantity.text = "quantity: " + itemMarket.quantity.toString()
        holder.buyerId.text = "buyerId: " + itemMarket.buyerId
        holder.sellerId.text = "sellerId: " + itemMarket.sellerId
        holder.price.text = "price: " + itemMarket.price

        holder.itemView.setOnClickListener {
            onItemClickListener(itemMarket)
        }
    }

    override fun getItemCount(): Int {
        return detalhesList.size
    }

    fun updateData(newProdutoList: List<Product>) {
        this.detalhesList = newProdutoList
        notifyDataSetChanged()
    }
}