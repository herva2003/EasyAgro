package com.puc.easyagro.ui.perfil.buttons

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.puc.easyagro.R
import com.puc.easyagro.apiServices.UserApi
import com.puc.easyagro.constants.Constants
import com.puc.easyagro.databinding.FragmentMinhasTarefasBinding
import com.puc.easyagro.datastore.UserPreferencesRepository
import com.puc.easyagro.ui.perfil.tarefas.TarefaAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration
import java.time.LocalDateTime

class MinhasTarefasFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TarefaAdapter

    private var _binding: FragmentMinhasTarefasBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentMinhasTarefasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.recyclerViewTarefas

        val numberOfColumns = 1
        recyclerView.layoutManager = GridLayoutManager(requireContext(), numberOfColumns)

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()

        adapter = TarefaAdapter(emptyList())

        binding.toolbar.btnArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.toolbar.screenName.text = "Minhas Terefas"

        recyclerView.adapter = adapter

        fetchDataFromServer()

        val pullToRefresh = binding.pullToRefresh
        pullToRefresh.setOnRefreshListener {
            fetchDataFromServer()
            pullToRefresh.isRefreshing = false
        }
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
                val response = apiService.getTarefa(userId).execute()
                if (response.isSuccessful) {
                    var tarefaList = response.body() ?: emptyList()

                    Log.d("user", "Culturas: $tarefaList")

                    val now = LocalDateTime.now()

                    tarefaList = tarefaList.sortedBy {
                        val tarefaDateTime = LocalDateTime.of(it.year, it.month, it.day, it.hour, it.minute)
                        kotlin.math.abs(Duration.between(tarefaDateTime, now).toMinutes())
                    }

                    launch(Dispatchers.Main) {
                        adapter.updateData(tarefaList)
                    }
                }
            } catch (e: Exception) {
                Log.e("user", "Exception during data fetch", e)
            }
        }
    }
}