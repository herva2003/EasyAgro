package com.puc.easyagro.ui.market.viewItemMarket

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.denzcoskun.imageslider.models.SlideModel
import com.puc.easyagro.R
import com.puc.easyagro.databinding.FragmentViewItemMarketBinding
import com.puc.easyagro.ui.constants.Constants
import com.puc.easyagro.ui.home.culturas.Cultura
import com.puc.easyagro.ui.home.culturas.CulturasApiDetalhe
import com.puc.easyagro.ui.home.culturas.CulturasFragmentDirections
import com.puc.easyagro.ui.market.Market
import com.puc.easyagro.ui.market.MarketApi
import com.puc.easyagro.ui.market.MarketApiDetalhe
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

        fetchDataFromServer(itemId)

        return binding.root
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
                            nome = detalhesItemMarket.nome,
                            preco = detalhesItemMarket.preco,
                            descricao = detalhesItemMarket.descricao,
                            categoria = detalhesItemMarket.categoria
                        )

                        Log.d("Resposta da API", detalhesItemMarket.toString())

                        launch(Dispatchers.Main) {
                            binding.txtPrecoValor.text = "R$ ${mercado.preco}"
                            binding.txtDescricaoValor.text = mercado.descricao
                            binding.txtCategoriaValor.text = mercado.categoria
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