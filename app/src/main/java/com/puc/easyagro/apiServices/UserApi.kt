package com.puc.easyagro.apiServices

import com.google.gson.JsonObject
import com.puc.easyagro.model.Cultura
import com.puc.easyagro.model.Market
import com.puc.easyagro.model.Tarefa
import com.puc.easyagro.model.Usuario
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {
    @POST("/user/")
    fun addUser(@Body user: Usuario): Call<ResponseBody>

    @PUT("/user/{id}")
    fun updateUser(@Path("id") id: String, @Body user: Usuario): Call<ResponseBody>

    @PUT("/user/{id}/tarefa")
    fun addTarefa2(@Path("id") id: String, @Body tarefa: Tarefa): Call<ResponseBody>

    @PUT("/user/{id}/tarefa")
    fun addTarefa(@Path("id") id: String, @Body itemUser: JsonObject): Call<Void>

    @GET("/user/{id}/tarefa")
    fun getTarefa(@Path("id") id: String): Call<List<Tarefa>>

}