package com.puc.easyagro.ui.perfil.login_cadastro.login

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

    interface ApiService {
        @POST("login")
        fun login(@Body request: LoginRequest): Call<LoginRequest>
    }