package com.puc.easyagro.ui.perfil

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puc.easyagro.R
import com.puc.easyagro.databinding.ItemOptionBinding
import com.puc.easyagro.model.Option

class PerfilAdapter(
    private val context: Context,
    private val onOptionClicked: (Option) -> Unit
) : RecyclerView.Adapter<PerfilAdapter.OptionViewHolder>() {

    private val options = listOf(
        Option(
            name = "Meus Anúncios",
            icon = R.drawable.fig_icon_grid_view
        ),
        Option(
            name = "Minhas Vendas",
            icon = R.drawable.fig_icon_sell
        ),
        Option(
            name = "Minhas Compras",
            icon = R.drawable.fig_icon_local_mall
        ),
        Option(
            name = "Favoritos",
            icon = R.drawable.fig_icon_favorite
        ),
        Option(
            name = "Carrinho",
            icon = R.drawable.fig_icon_shopping_cart
        ),
        Option(
            name = "Tarefas",
            icon = R.drawable.fig_icon_task
        ),
    )

    class OptionViewHolder(val binding: ItemOptionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(option: Option, onOptionClicked: (Option) -> Unit) {
            binding.optionItemName.text = option.name
            binding.optionItemIcon.setImageResource(option.icon)

            binding.root.setOnClickListener {
                onOptionClicked(option)
            }
        }
    }

    override fun getItemCount(): Int = options.size

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val option = options[position]
        holder.bind(option, onOptionClicked)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemOptionBinding.inflate(inflater, parent, false)

        return OptionViewHolder(binding)
    }
}