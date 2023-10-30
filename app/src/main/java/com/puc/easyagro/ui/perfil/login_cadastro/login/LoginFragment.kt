package com.puc.easyagro.ui.perfil.login_cadastro.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.puc.easyagro.R
import com.puc.easyagro.databinding.FragmentLoginBinding
import okhttp3.OkHttpClient
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()

            val action = LoginFragmentDirections.actionLoginFragmentToCadastroFragment()
            findNavController().navigate(action, navOptions)

        val login = binding.inputEmail
        val password = binding.inputSenha

        signIn(login, password)

        return binding.root
    }

    private fun signIn(login: AppCompatEditText, password: AppCompatEditText) {

        val client = OkHttpClient.Builder().build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-52-67-161-184.sa-east-1.compute.amazonaws.com:8080/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val loginRequest = LoginRequest(login, password)

        val call = apiService.login(loginRequest)
        call.enqueue(object : Callback<LoginRequest> {
            override fun onResponse(call: Call<LoginRequest>, response: Response<LoginRequest>) {
                if (response.isSuccessful) {

                    Log.d("token",response.body().toString())

                } else {
                    val errorBody = response.errorBody()?.string()
                    try {
                        val jsonError = errorBody?.let { JSONObject(it) }
                        val errorMessage = jsonError?.getString("message")
                        Log.e("LoginFragment", "Erro no login: $errorMessage")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<LoginRequest>, t: Throwable) {

                Log.e("LoginFragment", "Falha na chamada: ${t.message}")
            }
        })
    }
}