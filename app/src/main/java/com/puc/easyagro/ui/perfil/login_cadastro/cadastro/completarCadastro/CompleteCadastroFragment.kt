package com.puc.easyagro.ui.perfil.login_cadastro.cadastro.completarCadastro

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.puc.easyagro.R
import com.puc.easyagro.apiServices.UserApi
import com.puc.easyagro.constants.Constants
import com.puc.easyagro.databinding.FragmentCompleteCadastroBinding
import com.puc.easyagro.model.UserDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CompleteCadastroFragment : Fragment() {

    private lateinit var binding: FragmentCompleteCadastroBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCompleteCadastroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usuario: UserDTO? = arguments?.getParcelable("usuario")

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()



        binding.btnRegister.setOnClickListener {
            if (validateInputFields() && usuario != null) {
                if (validateInputFields()) {
                    val apelido = binding.inputApelido.text.toString()
                val nome = binding.inputNome.text.toString()
                val telefone = binding.inputTelefone.text.toString()

                usuario.nickname =apelido
                usuario.name = nome
                usuario.phoneNumber = telefone
                Log.d("12",usuario.toString())
                    val action = CompleteCadastroFragmentDirections.actionCompleteCadastroFragmentToAddressFragment2(usuario)
                    findNavController().navigate(action, navOptions)
                }
            }
        }

    }







    private fun validateInputFields(): Boolean {
        // Validation Rules
        val nomeValidation: Boolean = binding.inputNome.text.toString().trim().isNotEmpty()
        val enderecoValidation: Boolean = binding.inputEndereco.text.toString().trim().isNotEmpty()
        val telefoneValidation: Boolean = binding.inputTelefone.text.toString().trim().isNotEmpty()
        val apelidoValidation: Boolean = binding.inputApelido.text.toString().trim().isNotEmpty()

        // Validation Message
        val blankMessage = "Esse campo não pode ser vazio"

        if (!nomeValidation  ) {
            binding.inputNome.error = blankMessage
        }

        if (!enderecoValidation) {
            binding.inputEndereco.error = blankMessage
        }

        if (!telefoneValidation) {
            binding.inputTelefone.error = blankMessage
        }

        if (!apelidoValidation) {
            binding.inputApelido.error = blankMessage
        }


        if (!nomeValidation || !enderecoValidation || !telefoneValidation || !apelidoValidation) {
            Toast.makeText(activity, "Tente novamente!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

}