package com.puc.easyagro.ui.perfil.login_cadastro.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.puc.easyagro.R
import com.puc.easyagro.databinding.FragmentLoginBinding
import com.puc.easyagro.ui.home.culturas.detalhesPlanta.DetalhesCulturaAdapter
import com.puc.easyagro.ui.home.culturas.detalhesPlanta.DetalhesCulturaFragmentDirections

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()

            val action = LoginFragmentDirections.actionLoginFragmentToCadastroFragment()
            findNavController().navigate(action, navOptions)

        return binding.root
    }
}