package com.puc.easyagro.ui.perfil.buttons

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.puc.easyagro.R
import com.puc.easyagro.databinding.FragmentMinhaContaBinding
import com.puc.easyagro.datastore.UserPreferencesRepository

class MinhaContaFragment : Fragment() {

    private var _binding: FragmentMinhaContaBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentMinhaContaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()

        binding.btnArrow.setOnClickListener {
            findNavController().popBackStack()
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
}