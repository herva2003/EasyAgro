package com.puc.easyagro.ui.perfil.buttons.minhasCompras.comprasDetalhes

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.puc.easyagro.R
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

            val orderStatus = arguments?.getString("status")

            val pix = arguments?.getString("pix")

            Log.d("ComprasDetalhesFragment", "dados: $products")

            adapter = ComprasDetalhesAdapter(products) { _ -> }


            recyclerView.adapter = adapter

            binding.verPix.isVisible = orderStatus == "pending"

            binding.verPix.setOnClickListener{
                showCopyCodeDialog(pix!!)
            }
        } else {
            Log.e("ComprasDetalhesFragment", "Products JSON is null")
        }

    }

    private fun showCopyCodeDialog(codeToCopy: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.layout_custom_dialog, null)

        dialogView.findViewById<TextView>(R.id.textViewCode).text = codeToCopy

        dialogView.findViewById<Button>(R.id.btnCopy).setOnClickListener {
            val clipboardManager =
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Código", codeToCopy)
            clipboardManager.setPrimaryClip(clipData)

            Snackbar.make(
                dialogView,
                "Código copiado para a área de transferência",
                Snackbar.LENGTH_SHORT
            ).show()
        }

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.show()
    }
}
