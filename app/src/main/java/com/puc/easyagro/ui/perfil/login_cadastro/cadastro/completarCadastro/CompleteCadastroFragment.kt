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
                    updateACC(getFormData(), userId)
                }
            }
        }
    }

    private fun getFormData(): Usuario {
        val nome = binding.inputName.text.toString()
        val endereco = binding.inputAddress1.text.toString()
        val phoneNumber = binding.inputPhoneNumber.text.toString()

        return Usuario(nome = nome, phoneNumber = phoneNumber, endereco = endereco)
    }

    private fun updateACC(user: Usuario, userId: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(UserApi::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                Log.d("cad", "Atualizando conta: $userId")

                val response = apiService.updateUser(userId, user).execute()
                if (response.isSuccessful) {
                    Log.d("cad", "Conta atualizada com sucesso")

                    val action = CompleteCadastroFragmentDirections.actionCompleteCadastroFragmentToPerfilFragment()
                    findNavController().navigate(action)

                    launch(Dispatchers.Main) {
                        Toast.makeText(context, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
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
        val nameValidation: Boolean = binding.inputName.text.toString().trim().isNotEmpty()
        val address1Validation: Boolean = binding.inputAddress1.text.toString().trim().isNotEmpty()
        val phoneNumberValidation: Boolean = binding.inputPhoneNumber.text.toString().trim().isNotEmpty()

        // Validation Message
        val blankMessage = "Esse campo não pode ser vazio"

        if (!nameValidation  ) {
            binding.inputName.error = blankMessage
        }

        if (!address1Validation) {
            binding.inputAddress1.error = blankMessage
        }

        if (!phoneNumberValidation) {
            binding.inputPhoneNumber.error = blankMessage
        }

        if (!nameValidation || !address1Validation || !phoneNumberValidation) {
            Toast.makeText(activity, "Tente novamente!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}