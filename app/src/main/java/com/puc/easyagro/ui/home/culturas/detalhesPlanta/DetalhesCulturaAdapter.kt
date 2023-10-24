package com.puc.easyagro.ui.home.culturas.detalhesPlanta

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.puc.easyagro.R

class DetalhesCulturaAdapter(private var detalhesList: List<Any>, private val onItemClickListener: (String) -> Unit) :
    RecyclerView.Adapter<DetalhesCulturaAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewDetalhe: TextView = itemView.findViewById(R.id.text_view_detalhe)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_detalhe_cultura, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val detalhe = detalhesList[position]

        if (detalhe is String) {
            holder.textViewDetalhe.text = detalhe
        } else {
            holder.textViewDetalhe.text = "Tipo desconhecido: ${detalhe.javaClass.simpleName}"
        }

        holder.itemView.setOnClickListener {
            detalhe.let { it1 ->
                onItemClickListener(it1 as String)
                Log.d("DetalhesCulturaAdapter", "Item de cultura clicado: $detalhe")
            }
        }
    }

    override fun getItemCount(): Int {
        return detalhesList.size
    }

    fun updateData(newDetalhesList: List<Any>) {
        detalhesList = newDetalhesList
        notifyDataSetChanged()
    }
}

