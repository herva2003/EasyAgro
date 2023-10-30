package com.puc.easyagro.ui.market

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.puc.easyagro.R
import com.puc.easyagro.databinding.FragmentMarketBinding
import com.puc.easyagro.ui.constants.Constants
import com.puc.easyagro.ui.home.culturas.CulturasFragmentDirections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MarketFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MarketAdapter

    private var _binding: FragmentMarketBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {

        _binding = FragmentMarketBinding.inflate(inflater, container, false)
        recyclerView = binding.recyclerViewMarket

        val numberOfColumns = 2
        recyclerView.layoutManager = GridLayoutManager(requireContext(), numberOfColumns)

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()

        adapter = MarketAdapter(emptyList()) { itemId, itemString ->
            val action = MarketFragmentDirections.actionMarketFragmentToViewItemMarketFragment(itemId, itemString)
            findNavController().navigate(action, navOptions)
        }

        _binding?.btnAddProduto?.setOnClickListener {
            val action = MarketFragmentDirections.actionMarketFragmentToAddProdutoFragment()
            findNavController().navigate(action, navOptions)
        }

        recyclerView.adapter = adapter

        fetchDataFromServer()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = getCategoriasAdapter(requireContext())
        binding.categoriasSpinner.adapter = adapter
    }

    private fun fetchDataFromServer() {

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(MarketApi::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getItemsMarket().execute()
                if (response.isSuccessful) {
                    var marketList = response.body() ?: emptyList()

                    marketList = marketList.sortedBy { it.nome }

                    launch(Dispatchers.Main) {
                        adapter.updateData(marketList)
                    }
                }
            } catch (e: Exception) {
            }
        }
    }
}
