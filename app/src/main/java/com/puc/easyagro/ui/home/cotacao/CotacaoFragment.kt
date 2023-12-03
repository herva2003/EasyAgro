package com.puc.easyagro.ui.home.cotacao

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.puc.easyagro.R
import com.puc.easyagro.apiServices.CotacaoApi
import com.puc.easyagro.apiServices.CotacaoApiBasic
import com.puc.easyagro.constants.Constants
import com.puc.easyagro.databinding.FragmentCotacaoBinding
import com.puc.easyagro.model.Produto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CotacaoFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CotacaoAdapter

    private var _binding: FragmentCotacaoBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentCotacaoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.recyclerViewCotacao

        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()

        adapter = CotacaoAdapter(emptyList()) {}

        _binding?.btnArrow?.setOnClickListener {
            val action = CotacaoFragmentDirections.actionCotacaoFragmentToHomeFragment()
            findNavController().navigate(action, navOptions)
        }

        recyclerView.adapter = adapter


        val pullToRefresh = binding.pullToRefresh
        pullToRefresh.setOnRefreshListener {
            fetchDataFromServer()
            pullToRefresh.isRefreshing = false
        }

        val buscarCotacao = binding.buscarCotacao

        buscarCotacao.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                adapter.filter.filter(s.toString())
            }
        })
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
                    val produtosList = cepeaProducts?.products ?: emptyList()

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
