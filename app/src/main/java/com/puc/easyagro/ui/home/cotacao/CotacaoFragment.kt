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

        checkStatusCot()

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

    private fun checkStatusCot(){

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(CotacaoApi::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.checkStatusCot()
                Log.d("cot", "Resposta: $response")
                if (response.isSuccessful) {
                    val statusResponse = response.body()
                    val status = statusResponse?.statusCot ?: false
                    if (status) {
                        fetchDataFromServer()
                    } else {
                        getCotacoesApi()
                        updateStatusCot()
                    }
                }
            } catch (e: Exception) {
                Log.e("cot", "Deu erro, Resposta: $e")
            }
        }
    }

    private fun fetchDataFromServer(){

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(CotacaoApi::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.fetchDataFromMongoDB()
                Log.d("cot", "Resposta: $response")
                if (response.isSuccessful) {
                    val cotacoes = response.body() ?: emptyList()
                    Log.d("cot", "Cotações: $cotacoes")

                    // Extrair a lista de produtos de cada cotação
                    val produtosList = cotacoes.flatMap { it.produtos ?: emptyList() }

                    launch(Dispatchers.Main) {
                        adapter.updateData(produtosList)
                    }
                }
            } catch (e: Exception) {
                Log.e("cot", "Deu erro, Resposta: $e")
            }
        }
    }

    private fun getCotacoesApi() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.infosimples.com/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(CotacaoApiBasic::class.java)
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = service.getCotacao("3ugnDX4YiO4wNX0e_sfHxfLoyaY0yMi4n07srw0Y", "300")

                if (response.code == 200) {
                    val dados = response.data

                    sendCotacoesToMongo(dados)

                    Log.d("cot", "Todas as cotações: $dados")
                } else if (response.code in 600..799) {
                    var mensagem = "Um erro aconteceu. Leia para saber mais:\n"
                    mensagem += "Código: ${response.code} (${response.code_message})\n"
                    if (response.errors != null) {
                        mensagem += response.errors.joinToString("; ")
                    }
                }
            } catch (e: Exception) {
                Log.d("cot", "Erro durante a busca da cotação", e)
            }
        }
    }

    private fun sendCotacoesToMongo(dados: Any){
        Log.d("cot", "Iniciando sendCotacoesToMongo com dados: $dados")
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(CotacaoApi::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                Log.d("cot", "Enviando dados para MongoDB")
                val response = apiService.sendDataToMongoDB(dados)
                if (response.isSuccessful) {
                    Log.d("cot", "Dados enviados com sucesso: $response")
                    fetchDataFromServer()
                } else {
                    Log.d("cot", "Falha ao enviar dados, resposta: $response")
                }
            } catch (e: Exception) {
                Log.d("cot", "Exceção capturada: ", e)
            }
        }
    }

    private fun updateStatusCot(){

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(CotacaoApi::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.updateStatusCot()
                Log.d("cot", "Resposta: $response")
                if (!response.isSuccessful) {
                    Log.e("cot", "Falha ao atualizar statusCot")
                }
            } catch (e: Exception) {
                Log.e("cot", "Deu erro, Resposta: $e")
            }
        }
    }
}
