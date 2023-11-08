package com.puc.easyagro.ui.home.culturas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.puc.easyagro.R
import com.puc.easyagro.model.Cultura

class CulturasAdapter(private var culturasList: List<Cultura>, private val onItemClickListener: (String, String) -> Unit) :
    RecyclerView.Adapter<CulturasAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeCulturaTextView: TextView = itemView.findViewById(R.id.text_view_nome_cultura)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_cultura, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cultura = culturasList[position]
        holder.nomeCulturaTextView.text = cultura.nome

        holder.itemView.setOnClickListener {
            cultura._id?.let { id ->
                cultura.nome?.let { it1 -> onItemClickListener(id, it1) }
            }
        }

    }

    override fun getItemCount(): Int {
        return culturasList.size
    }

    fun updateData(newCulturasList: List<Cultura>) {
        culturasList = newCulturasList
        notifyDataSetChanged()
    }
}