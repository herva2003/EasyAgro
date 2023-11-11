package com.puc.easyagro.apiServices

import com.puc.easyagro.model.RegisterRequest
import com.puc.easyagro.model.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface RegisterApi {
    @POST("/user/")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>
}