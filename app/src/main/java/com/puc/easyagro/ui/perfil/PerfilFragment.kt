package com.puc.easyagro.ui.perfil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.puc.easyagro.R
import com.puc.easyagro.databinding.FragmentPerfilBinding
import com.puc.easyagro.datastore.UserPreferencesRepository

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
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

        val userPreferencesRepository = UserPreferencesRepository.getInstance(requireContext())
        if(userPreferencesRepository.token == ""){
            val action = PerfilFragmentDirections.actionPerfilFragmentToLoginFragment()
            findNavController().navigate(action, navOptions)
            return
        }

        binding.btnProfile.setOnClickListener {
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

        binding.btnTarefa.setOnClickListener {
            findNavController().navigate(R.id.action_perfilFragment_to_tarefaFragment)
        }
    }

}