package com.puc.easyagro.ui.perfil.login_cadastro.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import com.puc.easyagro.R
import com.puc.easyagro.databinding.FragmentLoginBinding
import com.puc.easyagro.ui.datastore.UserPreferencesRepository
import com.puc.easyagro.ui.perfil.models.LoginRequest
import com.puc.easyagro.ui.perfil.models.LoginResponse
import com.puc.easyagro.ui.perfil.services.api.ApiService
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import androidx.navigation.fragment.findNavController
import retrofit2.converter.gson.GsonConverterFactory

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()

        binding.btnEntrar.setOnClickListener {
            val login = binding.inputEmail.text!!.toString()
            val password = binding.inputSenha.text.toString()
            signIn(login, password)
        }


//            val action = LoginFragmentDirections.actionLoginFragmentToCadastroFragment()
//            findNavController().navigate(action, navOptions)

        return binding.root
    }

    private fun signIn(login: String, password: String) {
        // Configurar o cliente HTTP
        val client = OkHttpClient.Builder().build()

        // Configurar o objeto Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-52-67-161-184.sa-east-1.compute.amazonaws.com:8080/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val loginRequest = LoginRequest(login, password)

        // Fazer a chamada de login
        val call = apiService.login(loginRequest)
        call.enqueue(object : Callback<LoginResponse> {
            val userPreferencesRepository = UserPreferencesRepository.getInstance(requireContext())
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val accessToken = loginResponse?.token
                    Log.d("token", accessToken ?: "Token não disponível")
                    // Log.d("token", loginResponse.toString())
                    userPreferencesRepository.updateToken(accessToken.toString())
                    Log.d("token", userPreferencesRepository.token)
                    findNavController().navigate(R.id.action_loginFragment_to_marketFragment)
                } else {
                    // Tratar erro de login
                    val errorBody = response.errorBody()?.string()
                    try {
                        val jsonError = JSONObject(errorBody)
                        val errorMessage = jsonError.getString("message")
                        Log.e("LoginFragment", "Erro no login: $errorMessage")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // Tratar falha na chamada
                Log.e("LoginFragment", "Falha na chamada: ${t.message}")
            }
        })
    }





}
