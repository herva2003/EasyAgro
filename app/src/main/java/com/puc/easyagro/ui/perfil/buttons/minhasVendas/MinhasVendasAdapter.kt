package com.puc.easyagro.ui.perfil.buttons.minhasVendas

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.puc.easyagro.R
import com.puc.easyagro.model.Order
import com.puc.easyagro.model.Product
import com.puc.easyagro.model.ProdutosPix
import com.puc.easyagro.ui.perfil.buttons.minhasCompras.MinhasComprasAdapter
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.regex.Pattern

class MinhasVendasAdapter(private var vendasList: List<ProdutosPix>, private val onItemClickListener: (ProdutosPix) -> Unit) :
    RecyclerView.Adapter<MinhasVendasAdapter.ViewHolder>(), Filterable {

    var vendasListFiltered = vendasList

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val transictionId: TextView = itemView.findViewById(R.id.txt_transiction_id)
        val totalPrice: TextView = itemView.findViewById(R.id.txt_price_compras)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_compras, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemCompras = vendasListFiltered[position]
        holder.transictionId.text = itemCompras.productId
        holder.totalPrice.text = "R$ " + itemCompras.price.toString()

        holder.itemView.setOnClickListener {
            onItemClickListener(itemCompras)
        }
    }

    override fun getItemCount(): Int {
        return vendasListFiltered.size
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
                vendasListFiltered = if (charString.isEmpty()) {
                    vendasList
                } else {
                    val filteredList = ArrayList<ProdutosPix>()
                    for (produto in vendasList) {
                        val produtoNome = removeAccents(produto.productId?.toLowerCase() ?: "")
                        if (produtoNome.contains(charString)) {
                            filteredList.add(produto)
                        }
                    }
                    filteredList
                }
                Log.d("mkt", "Número de itens após a filtragem: ${vendasListFiltered.size}")
                val filterResults = FilterResults()
                filterResults.values = vendasListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                vendasListFiltered = ArrayList(filterResults.values as List<ProdutosPix>)
                Log.d("mkt", "Publicando resultados. Número de itens: ${vendasListFiltered.size}")
                notifyDataSetChanged()
            }
        }
    }

    fun getData(): List<ProdutosPix> {
        return vendasListFiltered
    }

    fun updateData(newProdutoList: List<ProdutosPix>) {
        this.vendasList = newProdutoList
        this.vendasListFiltered = newProdutoList
        notifyDataSetChanged()
    }
}