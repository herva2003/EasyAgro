package com.puc.easyagro.ui.perfil.buttons.minhaConta

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.puc.easyagro.R
import com.puc.easyagro.apiServices.UserApi
import com.puc.easyagro.constants.Constants
import com.puc.easyagro.databinding.FragmentMinhaContaBinding
import com.puc.easyagro.datastore.UserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MinhaContaFragment : Fragment() {

    private var _binding: FragmentMinhaContaBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentMinhaContaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchUserFromServer()

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()

        binding.btnArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnViewCadastro.setOnClickListener {
            val action = MinhaContaFragmentDirections.actionMinhaContaFragmentToMeuCadastroFragment()
            findNavController().navigate(action, navOptions)
        }

        val userPreferencesRepository = UserPreferencesRepository.getInstance(requireContext())
        if(userPreferencesRepository.token != ""){
            binding.btnLogin.setOnClickListener {
                userPreferencesRepository.updateToken("")
                val action = MinhaContaFragmentDirections.actionMinhaContaFragmentToHomeFragment()
                findNavController().navigate(action, navOptions)
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
                        binding.txtApelido.text = user?.nickname
                    }
                }
            } catch (e: Exception) {
                Log.e("taf", "Exception during data fetch", e)
            }
        }
    }
}