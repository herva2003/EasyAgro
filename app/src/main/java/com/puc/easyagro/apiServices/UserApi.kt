package com.puc.easyagro.apiServices

import com.puc.easyagro.model.Usuario
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {
    @POST("/user/games/")
    fun addUser(@Body user: Usuario): Call<ResponseBody>

    @PUT("/user/games/{id}")
    fun updateUser(@Path("id") id: String, @Body user: Usuario): Call<ResponseBody>
}