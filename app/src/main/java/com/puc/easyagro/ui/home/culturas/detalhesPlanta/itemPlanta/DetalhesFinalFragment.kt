package com.puc.easyagro.ui.home.culturas.detalhesPlanta.itemPlanta

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
import com.puc.easyagro.databinding.FragmentDetalhesFinalBinding
import com.puc.easyagro.constants.Constants
import com.puc.easyagro.apiServices.CulturasApiDetalheClicado
import com.puc.easyagro.model.Deficiencia
import com.puc.easyagro.model.Doenca
import com.puc.easyagro.model.Praga
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetalhesFinalFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DetalhesFinalAdapter

    private var _binding: FragmentDetalhesFinalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDetalhesFinalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.recyclerViewDetalhesFinal

        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        val itemClicked = arguments?.getString("itemClicked")
        val itemId = arguments?.getString("itemId")
        val itemString = arguments?.getString("itemString")

        val clickedItem = binding.toolbar.clickedItem
        clickedItem.text = itemClicked?.capitalize()

        val textViewNomeCultura = binding.toolbar.nomeCultura
        textViewNomeCultura.text = itemString?.capitalize()
        Log.d("dff", "nome planta ${textViewNomeCultura.text}")

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()

        adapter = DetalhesFinalAdapter(emptyList()) { item ->
            if (itemId != null && itemClicked != null) {
                val action = DetalhesFinalFragmentDirections.actionDetalhesFinalFragmentToDetalhesItemFragment(itemId, item, itemClicked)
                findNavController().navigate(action, navOptions)
            }
        }

        _binding?.toolbar?.btnArrow?.setOnClickListener {
            findNavController().popBackStack()
        }

        recyclerView.adapter = adapter

        if (itemClicked != null) {
            if (itemId != null) {
                fetchDataFromServer(itemId, itemClicked)
            }
        }

        val pullToRefresh = binding.pullToRefresh
        pullToRefresh.setOnRefreshListener {
            if (itemClicked != null) {
                if (itemId != null) {
                    fetchDataFromServer(itemId, itemClicked)
                }
            }
            pullToRefresh.isRefreshing = false
        }
    }

    private fun fetchDataFromServer(itemId: String, type: String) {

        Log.d("dff", "Sending itemId: $itemId")
        Log.d("dff", "Sending type: $type")

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(CulturasApiDetalheClicado::class.java)


        GlobalScope.launch(Dispatchers.IO) {
            try {
                when (type) {
                    "doencas" -> {
                        val response = apiService.getDoencas(itemId, type).execute()
                        processResponse(response)
                    }
                    "pragas" -> {
                        val response = apiService.getPragas(itemId, type).execute()
                        processResponse(response)
                    }
                    "deficiencias" -> {
                        val response = apiService.getDeficiencias(itemId, type).execute()
                        processResponse(response)
                    }
                }
            } catch (e: Exception) {
                Log.e("dff", "Erro de rede: ${e.message}")
            }
        }
    }

    private fun <T> processResponse(response: Response<List<T>>) {
        Log.d("dff", "Received response: $response")

        if (response.isSuccessful) {
            val detalhesCultura = response.body()

            if (detalhesCultura != null) {
                Log.d("dff", detalhesCultura.toString())

                // Atualize o adaptador com os novos dados
                val listaNomes = when (detalhesCultura.first()) {
                    is Doenca -> detalhesCultura.map { (it as Doenca).nome}
                    is Praga -> detalhesCultura.map { (it as Praga).nome}
                    is Deficiencia -> detalhesCultura.map { (it as Deficiencia).nome}
                    else -> emptyList()
                }

                activity?.runOnUiThread {

                    Log.d("dff", "Updating adapter with ${listaNomes.size} items")
                    adapter.updateData(listaNomes)
                }
            } else {
                Log.d("dff", "Received body: $detalhesCultura")
            }
        } else {
            Log.e("dff", "Código de status: ${response.code()}")
        }
    }
}
