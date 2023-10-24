package com.puc.easyagro.ui.perfil.login_cadastro.cadastro.completarCadastro

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.puc.easyagro.R
import com.puc.easyagro.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(){
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()

        binding.btnProx.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToAddressFragment()
            findNavController().navigate(action, navOptions)
        }

        return binding.root
    }
}