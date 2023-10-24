package com.puc.easyagro.ui.home.culturas.detalhesPlanta.itemPlanta

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.puc.easyagro.R

class DetalhesFinalAdapter(private var culturasList: List<String?>, private val onItemClickListener: (String) -> Unit) :
    RecyclerView.Adapter<DetalhesFinalAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeCulturaTextView: TextView = itemView.findViewById(R.id.text_view_nome)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_detalhe_final, parent, false)
        return ViewHolder(itemView).apply {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = culturasList[position]
                    item?.let { onItemClickListener(it) }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nomeCultura = culturasList[position]?.split(": ")?.get(0)
        holder.nomeCulturaTextView.text = nomeCultura

        val detalhe = culturasList[position]

        holder.itemView.setOnClickListener {
            detalhe.let { it1 ->
                onItemClickListener(it1 as String)
                Log.d("dff", "Item de cultura clicado: $detalhe")
            }
        }
    }

    override fun getItemCount(): Int {
        return culturasList.size
    }

    fun updateData(newData: List<String?>) {
        Log.d("dff", "Updating data. New size is ${newData.size}")
        culturasList = newData
        notifyDataSetChanged()
    }
}

