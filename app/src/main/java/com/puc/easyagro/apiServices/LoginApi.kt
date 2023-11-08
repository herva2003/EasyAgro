package com.puc.easyagro.apiServices

import com.puc.easyagro.model.LoginRequest
import com.puc.easyagro.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {
    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
}