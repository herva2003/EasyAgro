package com.puc.easyagro.ui.perfil.buttons.minhasCompras

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.puc.easyagro.R
import com.puc.easyagro.apiServices.UserApi
import com.puc.easyagro.constants.Constants
import com.puc.easyagro.databinding.FragmentMinhasComprasBinding
import com.puc.easyagro.datastore.UserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MinhasComprasFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MinhasComprasAdapter

    private var _binding: FragmentMinhasComprasBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentMinhasComprasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.recyclerViewMarket

        val numberOfColumns = 1
        recyclerView.layoutManager = GridLayoutManager(requireContext(), numberOfColumns)

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()

        adapter = MinhasComprasAdapter(emptyList()) { order ->
            val products = order.products
            val status = order.status
            val pix = order.pix

            val gson = Gson()
            val productsJson = gson.toJson(products)

            val orderId = order.id

            val action = MinhasComprasFragmentDirections.actionMinhasComprasFragmentToComprasDetalhesFragment(productsJson,status,pix)
            findNavController().navigate(action, navOptions)
        }

        binding.toolbar.btnArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.toolbar.screenName.text = "Minhas Compras"

        recyclerView.adapter = adapter

        fetchDataFromServer()

        val pullToRefresh = binding.pullToRefresh
        pullToRefresh.setOnRefreshListener {
            fetchDataFromServer()
            pullToRefresh.isRefreshing = false
        }

        val buscarProduto = binding.buscarProduto

        buscarProduto.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                adapter.filter.filter(s.toString())
            }
        })
    }

    private fun fetchDataFromServer() {

        val userPreferencesRepository = UserPreferencesRepository.getInstance(requireContext())
        val userId = userPreferencesRepository.userId

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(UserApi::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getMyOrdersById(userId).execute()
                if (response.isSuccessful) {
                    val carrinhoList = response.body() ?: emptyList()

                    Log.d("minhasCompras", "Produtos: $carrinhoList")

                    launch(Dispatchers.Main) {
                        adapter.updateData(carrinhoList)
                    }
                }
            } catch (e: Exception) {
                Log.e("minhasCompras", "Exception during data fetch", e)
            }
        }
    }
}