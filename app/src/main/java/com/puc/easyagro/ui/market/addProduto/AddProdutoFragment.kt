package com.puc.easyagro.ui.market.addProduto

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.puc.easyagro.R
import com.puc.easyagro.databinding.FragmentAddProdutoBinding
import com.puc.easyagro.ui.constants.Constants
import com.puc.easyagro.ui.market.Market
import com.puc.easyagro.ui.market.MarketApi
import com.puc.easyagro.ui.market.getCategoriasAdapter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AddProdutoFragment : Fragment() {

    private var _binding: FragmentAddProdutoBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {

        _binding = FragmentAddProdutoBinding.inflate(inflater, container, false)

        _binding?.btnArrow?.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnAnunciar.setOnClickListener {
            val produto = getFormData()
            sendDataToServer(produto)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = getCategoriasAdapter(requireContext())
        binding.categoriasSpinner.adapter = adapter
    }

    private fun getFormData(): Market {
        val nome = binding.tituloAnuncioInput.text.toString()
        val descricao = binding.descricaoInput.text.toString()
        val categoria = binding.categoriasSpinner.selectedItem.toString()
        val preco = binding.precoInput.text.toString().toDouble()

        return Market(nome = nome, preco = preco, categoria = categoria, descricao = descricao)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun sendDataToServer(produto: Market) {

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(MarketApi::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                println("Enviando produto: $produto") // Log antes de enviar
                val response = apiService.addProduct(produto).execute()
                if (response.isSuccessful) {
                    val gson = Gson()
                    val produtoInserido: Market = gson.fromJson(response.body()?.string(), Market::class.java)
                    produto._id = produtoInserido._id
                    println("Produto inserido com sucesso: $produtoInserido") // Log após inserção bem-sucedida
                    launch(Dispatchers.Main) {
                        Toast.makeText(context, "Produto adicionado com sucesso!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    println("Falha ao inserir produto: $response") // Log em caso de falha
                }
            } catch (e: Exception) {
                println("Exceção ao inserir produto: $e") // Log em caso de exceção
            }
        }
    }


}
