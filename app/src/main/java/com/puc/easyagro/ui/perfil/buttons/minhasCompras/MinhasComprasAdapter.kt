package com.puc.easyagro.ui.perfil.buttons.minhasCompras

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.puc.easyagro.R
import com.puc.easyagro.model.Market
import com.puc.easyagro.model.Order
import com.puc.easyagro.ui.market.MarketAdapter
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.regex.Pattern

class MinhasComprasAdapter(private var comprasList: List<Order>, private val onItemClickListener: (Order) -> Unit) :
    RecyclerView.Adapter<MinhasComprasAdapter.ViewHolder>(), Filterable {

    var comprasListFiltered = comprasList

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val transictionId: TextView = itemView.findViewById(R.id.txt_transiction_id)
        val totalPrice: TextView = itemView.findViewById(R.id.txt_price_compras)
        val status: TextView = itemView.findViewById(R.id.txt_status)
        val createdAt: TextView = itemView.findViewById(R.id.txt_data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_compras, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemCompras = comprasListFiltered[position]
        holder.transictionId.text = itemCompras.transitionId
        holder.totalPrice.text = "R$ " + itemCompras.totalPrice.toString()
        holder.status.text = itemCompras.status

        val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        originalFormat.timeZone = TimeZone.getTimeZone("UTC")
        val targetFormat = SimpleDateFormat("dd/MM/yyyy '-' HH:mm", Locale.US)
        targetFormat.timeZone = TimeZone.getDefault()
        val date = originalFormat.parse(itemCompras.createdAt)
        val formattedDate = targetFormat.format(date)
        holder.createdAt.text = formattedDate

        holder.itemView.setOnClickListener {
            onItemClickListener(itemCompras)
        }
    }

    override fun getItemCount(): Int {
        return comprasListFiltered.size
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
                comprasListFiltered = if (charString.isEmpty()) {
                    comprasList
                } else {
                    val filteredList = ArrayList<Order>()
                    for (produto in comprasList) {
                        val produtoNome = removeAccents(produto.id?.toLowerCase() ?: "")
                        if (produtoNome.contains(charString)) {
                            filteredList.add(produto)
                        }
                    }
                    filteredList
                }
                Log.d("mkt", "Número de itens após a filtragem: ${comprasListFiltered.size}")
                val filterResults = FilterResults()
                filterResults.values = comprasListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                comprasListFiltered = ArrayList(filterResults.values as List<Order>)
                Log.d("mkt", "Publicando resultados. Número de itens: ${comprasListFiltered.size}")
                notifyDataSetChanged()
            }
        }
    }

    fun getData(): List<Order> {
        return comprasListFiltered
    }

    fun updateData(newProdutoList: List<Order>) {
        this.comprasList = newProdutoList
        this.comprasListFiltered = newProdutoList
        notifyDataSetChanged()
    }
}
