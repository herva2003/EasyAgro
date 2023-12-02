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

    private lateinit var recyclerViewTarefas: RecyclerView
    private lateinit var adapterTarefas: TarefaAdapter

    private lateinit var recyclerViewOpcoes: RecyclerView
    private lateinit var adapterOpcoes: PerfilAdapter

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewTarefas = binding.recyclerViewPerfil
        val numberOfColumnsTarefas = 1
        recyclerViewTarefas.layoutManager = GridLayoutManager(requireContext(), numberOfColumnsTarefas)
        adapterTarefas = TarefaAdapter(emptyList())
        recyclerViewTarefas.adapter = adapterTarefas

        recyclerViewOpcoes = binding.recyclerViewOptions
        val numberOfColumnsOpcoes = 1
        recyclerViewOpcoes.layoutManager = GridLayoutManager(requireContext(), numberOfColumnsOpcoes)

        adapterOpcoes = PerfilAdapter(requireContext()) { option ->
            when (option.name) {
                "Meus Anúncios" -> findNavController().navigate(R.id.action_perfilFragment_to_meusAnunciosFragment)
                "Minhas Vendas" -> findNavController().navigate(R.id.action_perfilFragment_to_minhasVendasFragment)
                "Minhas Compras" -> findNavController().navigate(R.id.action_perfilFragment_to_minhasComprasFragment)
                "Favoritos" -> findNavController().navigate(R.id.action_perfilFragment_to_favoritosFragment)
                "Carrinho" -> findNavController().navigate(R.id.action_perfilFragment_to_carrinhoFragment)
                "Tarefas" -> findNavController().navigate(R.id.action_perfilFragment_to_minhasTarefasFragment)
            }
        }
        recyclerViewOpcoes.adapter = adapterOpcoes

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
                        adapterTarefas.updateData(tarefaList)
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