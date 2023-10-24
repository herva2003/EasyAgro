package com.puc.easyagro.ui.market

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.puc.easyagro.R
import com.puc.easyagro.databinding.FragmentMarketBinding

class MarketFragment : Fragment() {

    private var _binding: FragmentMarketBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {

        _binding = FragmentMarketBinding.inflate(inflater, container, false)

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()

        _binding?.btnAddProduto?.setOnClickListener {
            val action = MarketFragmentDirections.actionMarketFragmentToAddProdutoFragment()
            findNavController().navigate(action, navOptions)
        }

        return binding.root
    }
}