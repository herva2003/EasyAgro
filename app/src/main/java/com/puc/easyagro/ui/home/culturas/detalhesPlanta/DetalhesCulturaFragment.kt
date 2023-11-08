package com.puc.easyagro.ui.home.culturas.detalhesPlanta

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.puc.easyagro.R
import com.puc.easyagro.databinding.FragmentDetalhesCulturaBinding
import com.puc.easyagro.constants.Constants
import com.puc.easyagro.model.Cultura
import com.puc.easyagro.apiServices.CulturasApiDetalhe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetalhesCulturaFragment : Fragment() {

    private var _binding: FragmentDetalhesCulturaBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DetalhesCulturaAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentDetalhesCulturaBinding.inflate(inflater, container, false)
        recyclerView = binding.recyclerViewDetalhes

        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        // Recuperar o _id do argumento
        val itemId = arguments?.getString("itemId")
        val itemString = arguments?.getString("itemString")

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()

        adapter = DetalhesCulturaAdapter(emptyList()) { itemClicked ->
            if(itemId != null && itemString != null){
                val action = DetalhesCulturaFragmentDirections.actionDetalhesCulturaFragmentToDetalhesFinalFragment(itemClicked, itemId, itemString)
                findNavController().navigate(action, navOptions)
            }
        }

        recyclerView.adapter = adapter

        if (itemId != null) {
            fetchDataFromServer(itemId)
        }

        val pullToRefresh = binding.pullToRefresh
        pullToRefresh.setOnRefreshListener {
            if (itemId != null) {
                fetchDataFromServer(itemId)
            }
            pullToRefresh.isRefreshing = false
        }

        _binding?.btnArrow?.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    private fun fetchDataFromServer(itemId: String) {

        val baseUrl = Constants.BASE_URL
        val apiUrl = "$baseUrl$itemId/"

        val retrofit = Retrofit.Builder()
            .baseUrl(apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(CulturasApiDetalhe::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getCulturas(itemId).execute()
                if (response.isSuccessful) {
                    val detalhesCultura = response.body()

                    if (detalhesCultura != null) {
                        val cultura = Cultura(
                            _id = detalhesCultura._id,
                            nome = detalhesCultura.nome,
                            doencas = detalhesCultura.doencas?.map {},
                            pragas = detalhesCultura.pragas?.map {},
                            deficiencias = detalhesCultura.deficiencias?.map {}
                        )

                        Log.d("Resposta da API", detalhesCultura.toString())

                        activity?.runOnUiThread {

                            val todosTitulos = mutableListOf<String>()

                            cultura.doencas?.let { todosTitulos.add("doencas") }
                            cultura.pragas?.let { todosTitulos.add("pragas") }
                            cultura.deficiencias?.let { todosTitulos.add("deficiencias") }
                            todosTitulos.sort()

                            adapter.updateData(todosTitulos)
                        }
                    }
                } else {
                    Log.e("Resposta da API", "Código de status: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("Resposta da API", "Erro de rede: ${e.message}")
            }
        }
    }
}