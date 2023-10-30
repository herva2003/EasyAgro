package com.puc.easyagro.ui.market

import android.content.Context
import android.widget.ArrayAdapter

fun getCategoriasAdapter(context: Context): ArrayAdapter<String> {
    val categorias = arrayOf("Tudo", "Grãos", "Leguminosos", "Frutas", "Raizez", "Tubérculos")
    val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, categorias)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    return adapter
}