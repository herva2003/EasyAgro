package com.puc.easyagro.ui.perfil.buttons.minhasCompras.comprasDetalhes

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.puc.easyagro.databinding.FragmentComprasDetalhesBinding
import com.puc.easyagro.model.Order
import com.puc.easyagro.model.Product
import com.puc.easyagro.ui.perfil.buttons.minhasCompras.MinhasComprasAdapter
import com.puc.easyagro.ui.perfil.buttons.minhasCompras.MinhasComprasFragmentDirections

class ComprasDetalhesFragment : Fragment() {

    private var _binding: FragmentComprasDetalhesBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ComprasDetalhesAdapter

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentComprasDetalhesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.recyclerViewComprasDetalhes

        val numberOfColumns = 1
        recyclerView.layoutManager = GridLayoutManager(requireContext(), numberOfColumns)

        val productsJson = arguments?.getString("products")
        val orderId = arguments?.getString("orderId")
        Log.d("ComprasDetalhesFragment", "dados: $orderId")

        binding.btnArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        if (productsJson != null) {
            val gson = Gson()
            val productsType = object : TypeToken<List<Product>>() {}.type
            val products = gson.fromJson<List<Product>>(productsJson, productsType)

            Log.d("ComprasDetalhesFragment", "dados: $products")

            adapter = ComprasDetalhesAdapter(products) { _ ->}

//            binding.txtPedido.text = orderId

            recyclerView.adapter = adapter
        } else {
            Log.e("ComprasDetalhesFragment", "Products JSON is null")
        }
    }
}
