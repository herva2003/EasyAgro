package com.puc.easyagro.ui.perfil.login_cadastro.cadastro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.puc.easyagro.R
import com.puc.easyagro.databinding.FragmentCadastroBinding

class CadastroFragment : Fragment() {

    private var _binding: FragmentCadastroBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentCadastroBinding.inflate(layoutInflater, container, false)

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()

        binding.btnRegister.setOnClickListener {
            val action = CadastroFragmentDirections.actionCadastroFragmentToProfileFragment()
            findNavController().navigate(action, navOptions)
        }

        binding.btnEntrar.setOnClickListener {
            val action = CadastroFragmentDirections.actionCadastroFragmentToLoginFragment()
            findNavController().navigate(action, navOptions)
        }

        return binding.root
    }
}