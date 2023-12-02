package com.puc.easyagro.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.puc.easyagro.databinding.BottomSheetAddressFragmentBinding
import com.puc.easyagro.model.AddressDto

class AddressBottomSheetFragment(private val address: AddressDto) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetAddressFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetAddressFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Populate the address details
        binding.textCep.text = address.cep
        binding.textLocalidade.text = address.localidade
        binding.textUf.text = address.uf
        binding.textLogradouro.text = address.logradouro
        binding.textNumero.text = address.numero.toString()

        // You can add more details as needed
    }
}
