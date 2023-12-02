package com.puc.easyagro.ui.perfil

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
import com.puc.easyagro.databinding.FragmentPerfilBinding
import com.puc.easyagro.datastore.UserPreferencesRepository
import com.puc.easyagro.ui.perfil.tarefas.TarefaAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration
import java.time.LocalDateTime

class PerfilFragment : Fragment() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TarefaAdapter

    private var _binding: FragmentPerfilBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.recyclerViewPerfil

        val numberOfColumns = 1
        recyclerView.layoutManager = GridLayoutManager(requireContext(), numberOfColumns)

        adapter = TarefaAdapter(emptyList())

        recyclerView.adapter = adapter

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()

        val userPreferencesRepository = UserPreferencesRepository.getInstance(requireContext())
        if(userPreferencesRepository.token == ""){
            val action = PerfilFragmentDirections.actionPerfilFragmentToLoginFragment()
            findNavController().navigate(action, navOptions)
            return
        }

        binding.toolbar.btnProfile.setOnClickListener {
            val action = PerfilFragmentDirections.actionPerfilFragmentToMinhaContaFragment()
            findNavController().navigate(action, navOptions)
        }

        binding.btnMeusAnuncios.setOnClickListener {
            val action = PerfilFragmentDirections.actionPerfilFragmentToMeusAnunciosFragment()
            findNavController().navigate(action, navOptions)
        }

        binding.btnMinhasVendas.setOnClickListener {
            val action = PerfilFragmentDirections.actionPerfilFragmentToMinhasVendasFragment()
            findNavController().navigate(action, navOptions)
        }

        binding.btnMinhasCompras.setOnClickListener {
            val action = PerfilFragmentDirections.actionPerfilFragmentToMinhasComprasFragment()
            findNavController().navigate(action, navOptions)
        }

        binding.btnFavoritos.setOnClickListener {
            val action = PerfilFragmentDirections.actionPerfilFragmentToFavoritosFragment()
            findNavController().navigate(action, navOptions)
        }

        binding.btnCarrinho.setOnClickListener {
            val action = PerfilFragmentDirections.actionPerfilFragmentToCarrinhoFragment()
            findNavController().navigate(action, navOptions)
        }

        binding.btnTodasTarefas.setOnClickListener {
            val action = PerfilFragmentDirections.actionPerfilFragmentToMinhasTarefasFragment()
            findNavController().navigate(action, navOptions)
        }

        binding.btnTarefa.setOnClickListener {
            findNavController().navigate(R.id.action_perfilFragment_to_tarefaFragment)
        }

        fetchDataFromServer()
        fetchUserFromServer()
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

                    Log.d("taf", "Culturas: $tarefaList")

                    val now = LocalDateTime.now()

                    tarefaList = tarefaList.sortedBy {
                        val tarefaDateTime = LocalDateTime.of(it.year, it.month, it.day, it.hour, it.minute)
                        kotlin.math.abs(Duration.between(tarefaDateTime, now).toMinutes())
                    }

                    if (tarefaList.size > 2) {
                        tarefaList = tarefaList.take(2)
                    }

                    launch(Dispatchers.Main) {
                        adapter.updateData(tarefaList)
                    }
                }
            } catch (e: Exception) {
                Log.e("taf", "Exception during data fetch", e)
            }
        }
    }

    private fun fetchUserFromServer() {

        val userPreferencesRepository = UserPreferencesRepository.getInstance(requireContext())
        val userId = userPreferencesRepository.userId

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(UserApi::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getUser(userId).execute()
                if (response.isSuccessful) {
                    val user = response.body()
                    launch(Dispatchers.Main) {
                        binding.toolbar.txtApelido.text = user?.nickname
                    }
                }
            } catch (e: Exception) {
                Log.e("taf", "Exception during data fetch", e)
            }
        }
    }
}