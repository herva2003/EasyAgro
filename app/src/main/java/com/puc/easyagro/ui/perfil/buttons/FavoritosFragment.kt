package com.puc.easyagro.ui.perfil.buttons

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.puc.easyagro.R
import com.puc.easyagro.apiServices.UserApi
import com.puc.easyagro.constants.Constants
import com.puc.easyagro.databinding.FragmentFavoritosBinding
import com.puc.easyagro.datastore.UserPreferencesRepository
import com.puc.easyagro.model.Market
import com.puc.easyagro.ui.market.MarketAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FavoritosFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MarketAdapter

    private var _binding: FragmentFavoritosBinding? = null

    private val binding get() = _binding!!

    private var favoritosList: List<Market> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritosBinding.inflate(inflater, container, false)
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

        adapter = MarketAdapter(emptyList()) { itemId, _ ->
            val action =
                FavoritosFragmentDirections.actionFavoritosFragmentToViewItemMarketFragment(itemId)
            findNavController().navigate(action, navOptions)
        }

        binding.toolbar.btnArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.toolbar.screenName.text = "Anúncios Salvos"
        binding.toolbar.toolbar.setBackgroundColor(Color.parseColor("#09B187"))
        binding.toolbar.screenName.setTextColor(Color.parseColor("#FFFFFF"))
        binding.toolbar.btnArrow.drawable.setTint(Color.parseColor("#FFFFFF"))

        recyclerView.adapter = adapter

        if (favoritosList.isEmpty()) {
            fetchDataFromServer()
        }

        val pullToRefresh = binding.pullToRefresh
        pullToRefresh.setOnRefreshListener {
            fetchDataFromServer()
            pullToRefresh.isRefreshing = false
        }

//        val buscarProduto = binding.buscarProduto
//
//        buscarProduto.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//
//            override fun afterTextChanged(s: Editable?) {
//                adapter.filter.filter(s.toString())
//            }
//        })
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
                val response = apiService.getFavoritos(userId).execute()
                if (response.isSuccessful) {
                    favoritosList = response.body() ?: emptyList()

                    Log.d("car", "Favoritos: $favoritosList")

                    launch(Dispatchers.Main) {
                        updateUI()
                    }
                }
            } catch (e: Exception) {
                Log.e("car", "Exception during data fetch", e)
            }
        }
    }

    private fun updateUI() {
        if (favoritosList.isEmpty()) {
            binding.recyclerViewMarket.visibility = View.GONE
            binding.label1.visibility = View.VISIBLE
            binding.label2.visibility = View.VISIBLE
            binding.labelIcon.visibility = View.VISIBLE
        } else {
            binding.recyclerViewMarket.visibility = View.VISIBLE
            binding.label1.visibility = View.GONE
            binding.label2.visibility = View.GONE
            binding.labelIcon.visibility = View.GONE

            adapter.updateData(favoritosList)
        }
    }
}