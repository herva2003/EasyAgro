package com.puc.easyagro.ui.home.culturas

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.puc.easyagro.R
import com.puc.easyagro.apiServices.CulturasApi
import com.puc.easyagro.databinding.FragmentCulturasBinding
import com.puc.easyagro.constants.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CulturasFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CulturasAdapter

    private var _binding: FragmentCulturasBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentCulturasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.recyclerViewCulturas

        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()

        adapter = CulturasAdapter(emptyList()) { itemId, itemString ->
            val action = CulturasFragmentDirections.actionCulturasFragmentToDetalhesCulturaFragment(itemId, itemString)
            findNavController().navigate(action, navOptions)
        }

        _binding?.toolbar?.btnArrow?.setOnClickListener {
            val action = CulturasFragmentDirections.actionCulturasFragmentToHomeFragment()
            findNavController().navigate(action, navOptions)
        }

        _binding?.toolbar?.screenName?.text = "Culturas"

        recyclerView.adapter = adapter

        fetchDataFromServer()

        val pullToRefresh = binding.pullToRefresh
        pullToRefresh.setOnRefreshListener {
            fetchDataFromServer()
            pullToRefresh.isRefreshing = false
        }
    }

    private fun fetchDataFromServer() {

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(CulturasApi::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getCulturas().execute()
                if (response.isSuccessful) {
                    var culturasList = response.body() ?: emptyList()

                    Log.d("clt", "Culturas: $culturasList")

                    culturasList = culturasList.sortedBy { it.nome }

                    launch(Dispatchers.Main) {
                        adapter.updateData(culturasList)
                    }
                }
            } catch (e: Exception) {
                Log.e("clt", "Exception during data fetch", e)
            }
        }
    }
}