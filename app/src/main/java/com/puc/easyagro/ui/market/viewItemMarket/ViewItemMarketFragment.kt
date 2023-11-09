package com.puc.easyagro.ui.market.viewItemMarket

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.puc.easyagro.R
import com.puc.easyagro.constants.Constants
import com.puc.easyagro.model.Market
import com.puc.easyagro.apiServices.MarketApiDetalhe
import com.puc.easyagro.databinding.FragmentViewItemMarketBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ViewItemMarketFragment : Fragment() {

    private var _binding: FragmentViewItemMarketBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentViewItemMarketBinding.inflate(layoutInflater, container, false)

        val itemId = arguments?.getString("itemId")!!
        val itemString = arguments?.getString("itemString")

        val textViewNomeCultura = binding.txtNome
        textViewNomeCultura.text = itemString?.capitalize()

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()

        _binding?.btnArrow?.setOnClickListener {
            val action = ViewItemMarketFragmentDirections.actionViewItemMarketFragmentToMarketFragment()
            findNavController().navigate(action, navOptions)
        }

        binding.btnAddCarrinho.setOnClickListener{
            addItemCarrinho(itemId)
        }

        fetchDataFromServer(itemId)

        return binding.root
    }

    private fun addItemCarrinho(itemId: String){

    }
    private fun fetchDataFromServer(itemId: String) {

        val baseUrl = Constants.BASE_URL
        val apiUrl = "$baseUrl$itemId/"

        val retrofit = Retrofit.Builder()
            .baseUrl(apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(MarketApiDetalhe::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getItem(itemId).execute()
                if (response.isSuccessful) {
                    val detalhesItemMarket = response.body()

                    if (detalhesItemMarket != null) {
                        val mercado = Market(
                            _id = detalhesItemMarket._id,
                            name = detalhesItemMarket.name,
                            price = detalhesItemMarket.price,
                            description = detalhesItemMarket.description,
                            category = detalhesItemMarket.category
                        )

                        Log.d("Resposta da API", detalhesItemMarket.toString())

                        launch(Dispatchers.Main) {
                            binding.txtPrecoValor.text = "R$${mercado.price}"
                            binding.txtDescricaoValor.text = mercado.description
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