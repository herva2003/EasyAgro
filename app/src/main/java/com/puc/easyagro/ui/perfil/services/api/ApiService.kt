package com.puc.easyagro.ui.perfil.services.api

import com.puc.easyagro.ui.perfil.models.LoginRequest
import com.puc.easyagro.ui.perfil.models.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
}





