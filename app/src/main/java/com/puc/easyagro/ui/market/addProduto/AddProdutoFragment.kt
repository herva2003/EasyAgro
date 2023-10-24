package com.puc.easyagro.ui.market.addProduto

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.puc.easyagro.R
import com.puc.easyagro.databinding.FragmentAddProdutoBinding

class AddProdutoFragment : Fragment() {

    private var _binding: FragmentAddProdutoBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {

        _binding = FragmentAddProdutoBinding.inflate(inflater, container, false)

        _binding?.btnArrow?.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categorias = arrayOf("Categoria 1", "Categoria 2", "Categoria 3")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categoriasSpinner.adapter = adapter
    }
}
