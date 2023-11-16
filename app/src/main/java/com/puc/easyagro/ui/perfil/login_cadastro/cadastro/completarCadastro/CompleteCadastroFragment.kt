package com.puc.easyagro.ui.perfil.login_cadastro.cadastro.completarCadastro

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.puc.easyagro.apiServices.UserApi
import com.puc.easyagro.constants.Constants
import com.puc.easyagro.databinding.FragmentCompleteCadastroBinding
import com.puc.easyagro.datastore.UserViewModel
import com.puc.easyagro.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CompleteCadastroFragment : Fragment() {

    private lateinit var binding: FragmentCompleteCadastroBinding
    private val viewModel: UserViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCompleteCadastroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = viewModel.userId
        Log.d("cad", "UserId recebido: $userId")

        binding.btnRegister.setOnClickListener {
            if (validateInputFields()) {
                if (userId != null) {
                    updateACC(getFormData(), userId) {
                        val action = CompleteCadastroFragmentDirections.actionCompleteCadastroFragmentToHomeFragment()
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    private fun getFormData(): Usuario {
        val apelido = binding.inputApelido.text.toString()
        val nome = binding.inputNome.text.toString()
        val endereco = binding.inputEndereco.text.toString()
        val telefone = binding.inputTelefone.text.toString()
        val cpf = binding.inputCpf.text.toString()

        return Usuario(nome = nome, telefone = telefone, endereco = endereco, apelido = apelido, cpf = cpf)
    }

    private fun updateACC(user: Usuario, userId: String, onSuccess: () -> Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(UserApi::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                Log.d("cad", "Atualizando conta: $userId")

                val response = apiService.completeUser(userId, user).execute()
                if (response.isSuccessful) {
                    Log.d("cad", "Conta atualizada com sucesso")

                    launch(Dispatchers.Main) {
                        Toast.makeText(context, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
                        onSuccess()
                    }

                } else {
                    Log.d("cad", "Falha ao atualizar conta: $response")
                }

            }catch (e: Exception) {
                Log.d("cad", "Exception ao atualizar conta: ", e)
            }

        }
    }

    private fun validateInputFields(): Boolean {
        // Validation Rules
        val nomeValidation: Boolean = binding.inputNome.text.toString().trim().isNotEmpty()
        val enderecoValidation: Boolean = binding.inputEndereco.text.toString().trim().isNotEmpty()
        val telefoneValidation: Boolean = binding.inputTelefone.text.toString().trim().isNotEmpty()
        val apelidoValidation: Boolean = binding.inputApelido.text.toString().trim().isNotEmpty()
        val cpfValidation: Boolean = binding.inputCpf.text.toString().trim().isNotEmpty()

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

        if (!cpfValidation) {
            binding.inputCpf.error = blankMessage
        }

        if (!nomeValidation || !enderecoValidation || !telefoneValidation || !apelidoValidation || !cpfValidation) {
            Toast.makeText(activity, "Tente novamente!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}