package com.puc.easyagro.ui.home.culturas.detalhesPlanta.itemPlanta.detalhesItemPlanta

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.puc.easyagro.R

class DetalhesItemAdapter(private var culturasList: List<String>, private val onItemClickListener: (String) -> Unit) :
    RecyclerView.Adapter<DetalhesItemAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeCulturaTextView: TextView = itemView.findViewById(R.id.text_view_nome)
        val detalheTextView: TextView = itemView.findViewById(R.id.text_view_nome_descricao)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_detalhe_final, parent, false)
        return ViewHolder(itemView).apply {
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = culturasList[position]
        holder.nomeCulturaTextView.text = currentItem.split(":")[0]
        holder.detalheTextView.text = currentItem.split(":")[1]

        holder.detalheTextView.visibility =
            if (position == 0 || position == 1) View.VISIBLE else View.GONE
        holder.itemView.setOnClickListener { onItemClickListener(currentItem) }

        holder.itemView.setOnClickListener {
            if (position > 1) {
                holder.itemView.setOnClickListener {
                    holder.detalheTextView.visibility =
                        if (holder.detalheTextView.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                }
            }
        }

        if (position < 2) {
            holder.nomeCulturaTextView.textSize = 11f
            holder.detalheTextView.textSize = 17f
            holder.nomeCulturaTextView.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.grey_500
                )
            )
        } else {
            holder.nomeCulturaTextView.textSize = 17f
            holder.detalheTextView.textSize = 14f
        }
    }

    override fun getItemCount(): Int {
        return culturasList.size
    }

    fun updateData(newCulturasList: List<String>?) {
        if (newCulturasList != null) {
            culturasList = newCulturasList
        }
        Log.d("dif", "Dados atualizados: $culturasList")
        notifyDataSetChanged()
    }
}