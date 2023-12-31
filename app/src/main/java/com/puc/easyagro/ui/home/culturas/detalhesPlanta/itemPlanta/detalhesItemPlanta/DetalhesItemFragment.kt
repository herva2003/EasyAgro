package com.puc.easyagro.ui.home.culturas.detalhesPlanta.itemPlanta.detalhesItemPlanta

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.puc.easyagro.databinding.FragmentDetalhesItemBinding
import com.puc.easyagro.constants.Constants
import com.puc.easyagro.apiServices.CulturasApiItem
import com.puc.easyagro.model.Deficiencia
import com.puc.easyagro.model.Doenca
import com.puc.easyagro.model.NameRequestBody
import com.puc.easyagro.model.Praga
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URLEncoder

class DetalhesItemFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DetalhesItemAdapter

    private var _binding: FragmentDetalhesItemBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDetalhesItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = binding.imgCultura

        recyclerView = binding.recyclerViewDetalhes

        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        val itemClicked = arguments?.getString("itemClicked")
        val itemId = arguments?.getString("itemId")
        val item = arguments?.getString("item")

        val textViewNomeCultura = binding.toolbar.screenName
        textViewNomeCultura.text = item?.capitalize()

        adapter = DetalhesItemAdapter(emptyList()) {}

        _binding?.toolbar?.btnArrow?.setOnClickListener {
            findNavController().popBackStack()
        }

        recyclerView.adapter = adapter

        if (itemClicked != null) {
            if (itemId != null) {
                if (item != null) {
                    fetchDataFromServer(itemId, itemClicked, item)
                }
            }
        }
    }

    private fun fetchDataFromServer(itemId: String, type: String, item: String) {

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(CulturasApiItem::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = when(type) {
                    "doencas" -> apiService.getDoenca(itemId, type, item).execute()
                    "pragas" -> apiService.getPraga(itemId, type, item).execute()
                    "deficiencias" -> apiService.getDeficiencia(itemId, type, item).execute()
                    else -> throw IllegalArgumentException("Tipo desconhecido: $type")
                }

                if (response.isSuccessful) {
                    val detalhesCultura = response.body()
                    if (detalhesCultura != null) {
                        Log.d("dif", detalhesCultura.toString())
                        val detalhesList = when (detalhesCultura) {
                            is Doenca -> {
                                val imageUrl = detalhesCultura.imagem
                                withContext(Dispatchers.Main) {
                                    Picasso.get().load(imageUrl).into(imageView)
                                }
                                listOf(
                                    "Nome comum: ${detalhesCultura.nome}",
                                    "Agente Causal: ${detalhesCultura.agenteCausal}",
                                    "Descrição/Sintomas: ${detalhesCultura.descricao}",
                                    "Disseminação: ${detalhesCultura.disseminacao}",
                                    "Condições Favoráveis: ${detalhesCultura.condicoesFavoraveis}",
                                    "Controle: ${detalhesCultura.controle}",
                                )
                            }
                            is Praga -> {
                                val imageUrl = detalhesCultura.imagem
                                withContext(Dispatchers.Main) {
                                    Picasso.get().load(imageUrl).into(imageView)
                                }
                                listOf(
                                    "Nome comum: ${detalhesCultura.nome}",
                                    "Nome científico: ${detalhesCultura.nomeCientifico}",
                                    "Descrição: ${detalhesCultura.descricao}",
                                    "Sintomas/Danos: ${detalhesCultura.sintomasDanos}",
                                    "Disseminação: ${detalhesCultura.disseminacao}",
                                    "Condições Favoráveis: ${detalhesCultura.condicoesFavoraveis}",
                                    "Controle: ${detalhesCultura.controle}",
                                )
                            }
                            is Deficiencia -> {
                                val imageUrl = detalhesCultura.imagem
                                withContext(Dispatchers.Main) {
                                    Picasso.get().load(imageUrl).into(imageView)
                                }
                                listOf(
                                    "Nome comum: ${detalhesCultura.nome}",
                                    "Sintomas de deficiência: ${detalhesCultura.sintomas}",
                                )
                            }
                            else -> throw IllegalArgumentException("Tipo de detalhe desconhecido: ${detalhesCultura::class.java}")
                        }
                        activity?.runOnUiThread {
                            adapter.updateData(detalhesList)
                        }
                    } else {
                        Log.d("dif", "Corpo da resposta vazio")
                    }
                } else {
                    Log.e("dif", "Código de status da resposta: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("dif", "Erro de rede: ${e.message}")
            }
        }
    }

}