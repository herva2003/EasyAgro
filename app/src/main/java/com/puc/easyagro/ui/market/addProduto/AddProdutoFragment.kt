package com.puc.easyagro.ui.market.addProduto

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.load.engine.executor.GlideExecutor.UncaughtThrowableStrategy.LOG
import com.google.gson.Gson
import com.puc.easyagro.databinding.FragmentAddProdutoBinding
import com.puc.easyagro.constants.Constants
import com.puc.easyagro.model.Market
import com.puc.easyagro.apiServices.MarketApi
import com.puc.easyagro.datastore.UserPreferencesRepository
import com.puc.easyagro.model.MarketDTO
import com.puc.easyagro.ui.market.getCategoriasAdapter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AddProdutoFragment : Fragment() {

    private var _binding: FragmentAddProdutoBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddProdutoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.btnArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnAnunciar.setOnClickListener {
            if (validateInputFields()) {
                sendDataToServer(getFormData())
            }
        }

        val adapter = getCategoriasAdapter(requireContext())
        binding.categoriasSpinner.adapter = adapter

        binding.toolbar.screenName.text = "Inserir Anúncio"
    }

    private fun getFormData(): MarketDTO {
        val userPreferencesRepository = UserPreferencesRepository.getInstance(requireContext())
        val userId = userPreferencesRepository.userId
        Log.d("22", userId)
        val name = binding.anuncioInput.text.toString()
        val category = binding.categoriasSpinner.selectedItem.toString()
        val price = binding.precoInput.text.toString().toBigDecimal()
        val description = binding.descricaoInput.text.toString()

        return MarketDTO(name = name, price = price, category = category, description = description, userId = userId)
    }

    private fun validateInputFields(): Boolean {
        // Validation Rules
        val name: Boolean = binding.anuncioInput.text.toString().trim().isNotEmpty()
        val price: Boolean = binding.precoInput.text.toString().trim().isNotEmpty()
        val category: Boolean =
            binding.categoriasSpinner.selectedItem.toString().trim().isNotEmpty()
        val description: Boolean = binding.descricaoInput.text.toString().trim().isNotEmpty()

        val blankMessage = "Esse campo não pode ser vazio"

        if (!name) {
            binding.anuncioInput.error = blankMessage
        }

        if (!price) {
            binding.precoInput.error = blankMessage
        }

        if (!category) {
            val errorView = binding.categoriasSpinner.selectedView as? TextView
            errorView?.error = blankMessage
        }

        if (!description) {
            binding.descricaoInput.error = blankMessage
        }

        if (!name || !price || !category || !description) {
            Toast.makeText(activity, "Tente novamente!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun sendDataToServer(produto: MarketDTO) {

        val userPreferencesRepository = UserPreferencesRepository.getInstance(requireContext())

        val interceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .header("Authorization", "Bearer ${userPreferencesRepository.token}")
                .build()
            chain.proceed(request)
        }
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()

        val apiService = retrofit.create(MarketApi::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                Log.d("mkt", "Enviando produto: $produto")
                val response = apiService.addProduct(produto).execute()
                if (response.isSuccessful) {
                    val gson = Gson()
                    val produtoInserido: MarketDTO = gson.fromJson(response.body()?.string(), MarketDTO::class.java)
                    Log.d("mkt", "Produto inserido com sucesso: $produtoInserido")
                    launch(Dispatchers.Main) {
                        Toast.makeText(context, "Produto adicionado com sucesso!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("mkt", "Falha ao inserir produto: $response")
                }
            } catch (e: Exception) {
                Log.d("mkt", "Excessão ao inserir produto: $e")
            }
        }
    }

}
