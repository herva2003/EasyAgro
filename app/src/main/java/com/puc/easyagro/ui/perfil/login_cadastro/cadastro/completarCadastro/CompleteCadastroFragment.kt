package com.puc.easyagro.ui.perfil.login_cadastro.cadastro.completarCadastro

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.puc.easyagro.R
import com.puc.easyagro.apiServices.UserApi
import com.puc.easyagro.constants.Constants
import com.puc.easyagro.databinding.FragmentCompleteCadastroBinding
import com.puc.easyagro.datastore.UserViewModel
import com.puc.easyagro.model.UserDTO
import com.puc.easyagro.model.UserUpdateDTO
import com.puc.easyagro.model.Usuario
import com.puc.easyagro.ui.perfil.login_cadastro.cadastro.CadastroFragmentDirections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.log

class CompleteCadastroFragment : Fragment() {

    private lateinit var binding: FragmentCompleteCadastroBinding
    private val viewModel: UserViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCompleteCadastroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usuario: UserDTO? = arguments?.getParcelable("usuario")



        binding.btnRegister.setOnClickListener {
            if (validateInputFields() && usuario != null) {
                val apelido = binding.inputApelido.text.toString()
                val nome = binding.inputNome.text.toString()
                val telefone = binding.inputTelefone.text.toString()

                usuario.nickname =apelido
                usuario.name = nome
                usuario.phoneNumber = telefone
                Log.d("12",usuario.toString())

                registerUser(usuario)
            }
        }

    }

    private fun registerUser(conta: UserDTO) {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(UserApi::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                Log.d("cad", "Criando conta: ${conta.login}")
                val response = apiService.addUser(conta).execute()
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
                    }
                    findNavController().navigate(R.id.action_completeCadastroFragment_to_loginFragment)
                } else {
                    Log.d("cad", "Falha ao criar conta: $response")

                    // Se desejar mostrar um Toast apenas em caso de falha, adicione o Toast aqui
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Falha ao criar conta. Tente novamente.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("cad", "Erro ao criar conta: ${e.message}")
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