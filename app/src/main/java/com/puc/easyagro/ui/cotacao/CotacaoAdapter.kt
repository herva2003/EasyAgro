package com.puc.easyagro.ui.cotacao

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.puc.easyagro.R
import com.puc.easyagro.model.Cotacao
import com.puc.easyagro.model.Produto

class CotacaoAdapter(private var produtosList: List<Produto>, private val onItemClickListener: () -> Unit) :
    RecyclerView.Adapter<CotacaoAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeCotacao: TextView = itemView.findViewById(R.id.text_view_nome_cotacao)
        val produtoCotacao: TextView = itemView.findViewById(R.id.text_view_produto_cotacao)
        val localCotacao: TextView = itemView.findViewById(R.id.text_view_local_cotacao)
        val valorCotacao: TextView = itemView.findViewById(R.id.text_view_valor_cotacao)
        val unidadeCotacao: TextView = itemView.findViewById(R.id.text_view_unidade_cotacao)
        val dataCotacao: TextView = itemView.findViewById(R.id.text_view_data_cotacao)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_cotacao, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val produto = produtosList[position]
        holder.nomeCotacao.text = produto.nome
        holder.produtoCotacao.text = produto.produto
        holder.localCotacao.text = "  -  ${produto.local}"
        holder.valorCotacao.text = produto.valor
        holder.unidadeCotacao.text = "(${produto.unidade})"
        holder.dataCotacao.text = "Atualizado em ${produto.data}"
    }


    override fun getItemCount(): Int {
        return produtosList.size
    }

    fun updateData(newProdutosList: List<Produto>) {
        produtosList = newProdutosList
        notifyDataSetChanged()
    }
}
