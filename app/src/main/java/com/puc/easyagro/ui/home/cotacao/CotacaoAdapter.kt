package com.puc.easyagro.ui.home.cotacao

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.puc.easyagro.R
import com.puc.easyagro.model.Cotacao
import com.puc.easyagro.model.Produto
import java.text.Normalizer
import java.util.regex.Pattern

class CotacaoAdapter(private var produtosList: List<Produto>, private val onItemClickListener: () -> Unit) :
    RecyclerView.Adapter<CotacaoAdapter.ViewHolder>(), Filterable {

    var produtosListFiltered = produtosList

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
        val produto = produtosListFiltered[position]
        holder.nomeCotacao.text = produto.nome
        holder.produtoCotacao.text = produto.produto
        holder.localCotacao.text = "  -  ${produto.local}"
        holder.valorCotacao.text = produto.valor
        holder.unidadeCotacao.text = "(${produto.unidade})"
        holder.dataCotacao.text = "Atualizado em ${produto.data}"
    }

    override fun getItemCount(): Int {
        return produtosListFiltered.size
    }

    fun removeAccents(s: String): String {
        val normalized = Normalizer.normalize(s, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(normalized).replaceAll("")
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = removeAccents(charSequence.toString().toLowerCase())
                produtosListFiltered = if (charString.isEmpty()) {
                    produtosList
                } else {
                    val filteredList = ArrayList<Produto>()
                    for (produto in produtosList) {
                        val produtoNome = removeAccents(produto.produto?.toLowerCase() ?: "")
                        if (produtoNome.contains(charString)) {
                            filteredList.add(produto)
                        }
                    }
                    filteredList
                }
                Log.d("cot", "Número de itens após a filtragem: ${produtosListFiltered.size}")
                val filterResults = FilterResults()
                filterResults.values = produtosListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                produtosListFiltered = filterResults.values as ArrayList<Produto>
                Log.d("cot", "Publicando resultados. Número de itens: ${produtosListFiltered.size}")
                notifyDataSetChanged()
            }
        }
    }

    fun updateData(newProdutosList: List<Produto>) {
        this.produtosList = newProdutosList
        this.produtosListFiltered = newProdutosList
        notifyDataSetChanged()
    }
}