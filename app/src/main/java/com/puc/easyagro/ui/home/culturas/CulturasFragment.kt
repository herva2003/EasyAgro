package com.puc.easyagro.ui.home.culturas

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.puc.easyagro.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CulturasFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CulturasAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_culturas, container, false)
        recyclerView = rootView.findViewById(R.id.recycler_view_culturas)

        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        adapter = CulturasAdapter(emptyList())
        recyclerView.adapter = adapter

        // Iniciar a chamada de rede de forma assíncrona
        fetchDataFromServer()

        return rootView
    }

    private fun fetchDataFromServer() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://172.16.225.44:8080/games/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(CulturasApi::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getCulturas().execute()
                if (response.isSuccessful) {
                    val culturasList = response.body() ?: emptyList()
                    // Adicionar log para registrar a resposta da API
                    Log.d("Resposta da API", culturasList.toString())
                    // Atualizar o adaptador na thread principal
                    launch(Dispatchers.Main) {
                        adapter.updateData(culturasList)
                    }
                } else {
                    // Lida com erros de resposta aqui
                    Log.e("Erro na API", "Código de status: ${response.code()}")
                }
            } catch (e: Exception) {
                // Lida com erros de rede ou exceções aqui
                Log.e("Erro na API", "Erro de rede: ${e.message}")
            }
        }
    }
}
