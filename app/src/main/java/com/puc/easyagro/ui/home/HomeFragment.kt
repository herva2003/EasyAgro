package com.puc.easyagro.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.puc.easyagro.R
import com.puc.easyagro.apiServices.CotacaoApi
import com.puc.easyagro.constants.Constants
import com.puc.easyagro.databinding.FragmentHomeBinding
import com.puc.easyagro.ui.home.cotacao.CotacaoAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CotacaoAdapter

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.recyclerViewHome

        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        adapter = CotacaoAdapter(emptyList()) {}

        recyclerView.adapter = adapter

        fetchDataFromServer()

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()

        _binding?.btnCulturas?.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToCulturasFragment()
            findNavController().navigate(action, navOptions)
        }

        _binding?.btnCotacao?.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToCotacaoFragment()
            findNavController().navigate(action, navOptions)
        }
    }

    private fun fetchDataFromServer() {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(CotacaoApi::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getProductsCepea()

                if (response.isSuccessful) {
                    val cepeaProducts = response.body()
                    var produtosList = cepeaProducts?.products ?: emptyList()

                    if (produtosList.size > 2) {
                        produtosList = produtosList.take(2)
                    }
                    withContext(Dispatchers.Main) {
                        adapter.updateData(produtosList)
                    }
                } else {
                    // Trate o erro
                    Log.e("cot", "Erro na resposta: ${response.code()}")
                }
            } catch (e: Exception) {
                // Trate a exceção
                Log.e("cot", "Deu erro na requisição", e)
            }
        }
    }
}