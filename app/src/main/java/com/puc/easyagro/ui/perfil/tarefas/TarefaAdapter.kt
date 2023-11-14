package com.puc.easyagro.ui.perfil.tarefas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.puc.easyagro.R
import com.puc.easyagro.model.Tarefa

class TarefaAdapter(private var tarefaList: List<Tarefa>) :
    RecyclerView.Adapter<TarefaAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tarefaTitle: TextView = itemView.findViewById(R.id.tarefa_title)
        val tarefaDate: TextView = itemView.findViewById(R.id.tarefa_date)
        val tarefaTime: TextView = itemView.findViewById(R.id.tarefa_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_tarefa, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemTarefa = tarefaList[position]
        holder.tarefaTitle.text = itemTarefa.titleNotification
        holder.tarefaDate.text = "${itemTarefa.day}/${itemTarefa.month}/${itemTarefa.year}"
        holder.tarefaTime.text = "${itemTarefa.hour}:${itemTarefa.minute}"
    }

    override fun getItemCount(): Int {
        return tarefaList.size
    }

    fun updateData(newTarefaList: List<Tarefa>) {
        this.tarefaList = newTarefaList
        notifyDataSetChanged()
    }
}