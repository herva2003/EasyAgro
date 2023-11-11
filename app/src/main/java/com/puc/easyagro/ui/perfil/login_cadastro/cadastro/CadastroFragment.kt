package com.puc.easyagro.ui.perfil.login_cadastro.cadastro

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.puc.easyagro.R
import com.puc.easyagro.apiServices.LoginApi
import com.puc.easyagro.apiServices.RegisterApi
import com.puc.easyagro.apiServices.UserApi
import com.puc.easyagro.constants.Constants
import com.puc.easyagro.databinding.FragmentCadastroBinding
import com.puc.easyagro.datastore.UserPreferencesRepository
import com.puc.easyagro.datastore.UserViewModel
import com.puc.easyagro.model.RegisterRequest
import com.puc.easyagro.model.RegisterResponse
import com.puc.easyagro.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CadastroFragment : Fragment() {

    private var _binding: FragmentCadastroBinding? = null
    val viewModel: UserViewModel by activityViewModels()
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentCadastroBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = binding.inputEmail.text.toString()
        val password = binding.inputSenha.text.toString()

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()

        binding.btnRegister.setOnClickListener {
            if (validateInputFields()) {
                registerUser(getFormData())
            }
        }

        binding.btnEntrar.setOnClickListener {
            val action = CadastroFragmentDirections.actionCadastroFragmentToLoginFragment()
            findNavController().navigate(action, navOptions)
        }
    }

    private fun getFormData(): Usuario {
        val email = binding.inputEmail.text.toString()
        val senha = binding.inputSenha.text.toString()

        return Usuario(login = email, password = senha)
    }

    private fun registerUser(conta: Usuario) {
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
                    val gson = Gson()
                    val contaInserida: Usuario = gson.fromJson(response.body()?.string(), Usuario::class.java)
                    conta._id = contaInserida._id
                    Log.d("cad", "Conta criada com sucesso: ${contaInserida._id}")
                    val action = contaInserida._id?.let {
                        viewModel.userId = it
                        CadastroFragmentDirections.actionCadastroFragmentToCompleteCadastroFragment()
                    }
                    if (action != null) {
                        withContext(Dispatchers.Main) {
                            findNavController().navigate(action)
                            Toast.makeText(context, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.d("cad", "Falha ao criar conta: $response")
                }
            } catch (_: Exception) {
            }
        }
    }


    private fun validateInputFields(): Boolean {
        // Validation Rules
        val emailValidation: Boolean = binding.inputEmail.text.toString().trim().isNotEmpty()
        val passwordValidation: Boolean = binding.inputSenha.text.toString().isNotEmpty()
        val confirmPasswordValidation: Boolean =
            binding.inputSenhaConfirm.text.toString().isNotEmpty()
        val passwordSameValidation: Boolean =
            binding.inputSenha.text.toString() == binding.inputSenhaConfirm.text.toString()

        // Validation Message
        val blankMessage = "Esse campo não pode ser vazio"
        val passwordSameMessage = "As senhas devem ser iguais"

        if (!emailValidation) {
            binding.inputEmail.error = blankMessage
        }

        if (!passwordValidation) {
            binding.inputSenha.error = blankMessage
        }

        if (!confirmPasswordValidation) {
            binding.inputSenhaConfirm.error = blankMessage
        }

        if (!passwordSameValidation) {
            binding.inputSenha.error = passwordSameMessage
            binding.inputSenhaConfirm.error = passwordSameMessage
        }

        if (!emailValidation || !passwordValidation || !confirmPasswordValidation || !passwordSameValidation) {
            Toast.makeText(activity, "Tente novamente!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}