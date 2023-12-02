package com.puc.easyagro.ui.market

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.puc.easyagro.R
import com.puc.easyagro.model.Market
import com.squareup.picasso.Picasso
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.regex.Pattern

class MarketAdapter(private var marketList: List<Market>, private val onItemClickListener: (String, String) -> Unit) :
    RecyclerView.Adapter<MarketAdapter.ViewHolder>(), Filterable {

    var marketListFiltered = marketList

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeItemMarket: TextView = itemView.findViewById(R.id.nome_item_market)
        val categoriaItemMarket: TextView = itemView.findViewById(R.id.categoria_item_market)
        val precoItemMarket: TextView = itemView.findViewById(R.id.preco_item_market)
        val createdAt: TextView = itemView.findViewById(R.id.data_publicacao)
        val imagem: ImageView = itemView.findViewById(R.id.imgAnuncio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_market, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemMarket = marketListFiltered[position]
        holder.nomeItemMarket.text = itemMarket.name
        holder.categoriaItemMarket.text = itemMarket.category
        holder.precoItemMarket.text = "R$${itemMarket.price.toString()}"

        if (itemMarket.images?.isNotEmpty() == true) {
            val imageUrl = itemMarket.images[0]
            Picasso.get().load(imageUrl).into(holder.imagem)
        }

        val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        originalFormat.timeZone = TimeZone.getTimeZone("UTC")
        val targetFormat = SimpleDateFormat("dd/MM/yyyy '-' HH:mm", Locale.US)
        targetFormat.timeZone = TimeZone.getDefault()
        val date = originalFormat.parse(itemMarket.createdAt)
        val formattedDate = targetFormat.format(date)
        holder.createdAt.text = formattedDate

        holder.itemView.setOnClickListener {
            itemMarket.id?.let { id ->
                itemMarket.name?.let { it1 -> onItemClickListener(id, it1) }
            }
        }
    }

    override fun getItemCount(): Int {
        return marketListFiltered.size
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
                Log.d("mkt", "Texto de busca: $charString")
                    marketListFiltered = if (charString.isEmpty()) {
                    marketList
                } else {
                    val filteredList = ArrayList<Market>()
                    for (produto in marketList) {
                        val produtoNome = removeAccents(produto.name?.toLowerCase() ?: "")
                        if (produtoNome.contains(charString)) {
                            filteredList.add(produto)
                        }
                    }
                    filteredList
                }
                Log.d("mkt", "Número de itens após a filtragem: ${marketListFiltered.size}")
                val filterResults = FilterResults()
                filterResults.values = marketListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                marketListFiltered = ArrayList(filterResults.values as List<Market>)
                Log.d("mkt", "Publicando resultados. Número de itens: ${marketListFiltered.size}")
                notifyDataSetChanged()
            }
        }
    }

    fun getData(): List<Market> {
        return marketListFiltered
    }

    fun updateData(newProdutoList: List<Market>) {
        this.marketList = newProdutoList
        this.marketListFiltered = newProdutoList
        notifyDataSetChanged()
    }
}