package com.puc.easyagro.ui.market.viewItemMarket

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.gson.JsonObject
import com.puc.easyagro.R
import com.puc.easyagro.apiServices.CarrinhoApi
import com.puc.easyagro.apiServices.MarketApi
import com.puc.easyagro.constants.Constants
import com.puc.easyagro.model.Market
import com.puc.easyagro.apiServices.MarketApiDetalhe
import com.puc.easyagro.apiServices.UserApi
import com.puc.easyagro.databinding.FragmentViewItemMarketBinding
import com.puc.easyagro.datastore.UserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ViewItemMarketFragment : Fragment() {

    private var _binding: FragmentViewItemMarketBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentViewItemMarketBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        binding.btnCoracao.setOnClickListener{
            addItemFavoritos(itemId)
        }

        isItemFavorito(itemId)
        fetchDataFromServer(itemId)
    }

    private fun addItemFavoritos(itemId: String){
        val userPreferencesRepository = UserPreferencesRepository.getInstance(requireContext())

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(CarrinhoApi::class.java)
        val userId = userPreferencesRepository.userId

        val itemUser = JsonObject()
        itemUser.addProperty("itemId", itemId)
        itemUser.addProperty("userId", userId)

        val call = apiService.addItemFavoritos(itemUser)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("mkt","Item adicionado aos favoritos")
                    Toast.makeText(context, "Produto adicionado com sucesso!", Toast.LENGTH_SHORT).show()

                    val heartIcon: ImageView = binding.btnCoracao
                    val heartDrawable = context?.let { ContextCompat.getDrawable(it, R.drawable.coracao) } as VectorDrawable
                    heartDrawable.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(context!!, R.color.red_700), PorterDuff.Mode.SRC_IN)
                    heartIcon.setImageDrawable(heartDrawable)
                } else {
                    response.errorBody()?.string()
                    Log.d("mkt","Erro ao adicionar item aos favoritos: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, e: Throwable) {
                Log.e("mkt", "Falha na chamada: ${e.message}")
            }
        })
    }

    private fun isItemFavorito(itemId: String) {

        val userPreferencesRepository = UserPreferencesRepository.getInstance(requireContext())
        val userId = userPreferencesRepository.userId

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(UserApi::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.isItemFavorito(userId, itemId).execute()
                if (response.isSuccessful) {
                    val isFavorito = response.body()?.isFavorito ?: false

                    launch(Dispatchers.Main) {
                        if (isFavorito) {
                            val heartIcon: ImageView = binding.btnCoracao
                            val heartDrawable = context?.let { ContextCompat.getDrawable(it, R.drawable.coracao) } as VectorDrawable
                            heartDrawable.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(requireContext(), R.color.red_700), PorterDuff.Mode.SRC_IN)
                            heartIcon.setImageDrawable(heartDrawable)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("mkt", "Exception during data fetch", e)
            }
        }
    }



    private fun addItemCarrinho(itemId: String) {
        val userPreferencesRepository = UserPreferencesRepository.getInstance(requireContext())

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(CarrinhoApi::class.java)
        val userId = userPreferencesRepository.userId

        val itemUser = JsonObject()
        itemUser.addProperty("itemId", itemId)
        itemUser.addProperty("userId", userId)

        val call = apiService.addItemCarrinnho(itemUser)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("mkt","Item adicionado ao carrinho")
                    Toast.makeText(context, "Produto adicionado com sucesso!", Toast.LENGTH_SHORT).show()
                } else {
                    response.errorBody()?.string()
                    Log.d("mkt","Erro ao adicionar item ao carrinho: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, e: Throwable) {
                Log.e("mkt", "Falha na chamada: ${e.message}")
            }
        })
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

                        Log.d("mkt", detalhesItemMarket.toString())

                        launch(Dispatchers.Main) {
                            binding.txtPrecoValor.text = "R$${mercado.price}"
                            binding.txtDescricaoValor.text = mercado.description
                        }
                    }
                } else {
                    Log.e("mkt", "Código de status: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("mkt", "Erro de rede: ${e.message}")
            }
        }
    }
}