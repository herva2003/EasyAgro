package com.puc.easyagro.apiServices

import com.google.gson.JsonObject
import com.puc.easyagro.model.Market
import com.puc.easyagro.model.Task
import com.puc.easyagro.model.UserUpdateDTO
import com.puc.easyagro.model.Usuario
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {
    @POST("/user/")
    fun addUser(@Body user: Usuario): Call<ResponseBody>

    @PUT("user/update/{userId}")
    fun completeUser(@Query("userId") id: String, @Body user: UserUpdateDTO): Call<ResponseBody>

    @POST("user/myTasks/add/{userId}")
    fun addTarefa(@Query("userId") userId: String, @Body task: Task): Call<Void>

    @GET("user/myTasks/{userId}")
    fun getTarefa(@Path("userId") id: String): Call<List<Task>>

    // get user by id
    @GET("user/{id}")
    fun getUser(@Path("id") id: String): Call<Usuario>

    @PUT("user/update/{userId}")
    fun updateUser(@Path("userId") id: String, @Body user: UserUpdateDTO): Call<ResponseBody>

    @GET("user/cart/{userId}")
    fun getItemCarrinho(@Path("userId") id: String): Call<List<Market>>


// Anúncios
    @GET("api/products/{userId}")
    fun getMeusAnuncios(@Path("userId") id: String): Call<List<Market>>


    @GET("user/myFavorites/{userId}")
    fun getFavoritos(@Path("userId") id: String): Call<List<Market>>



    // n sei o q faz, dps vc me explica

    data class FavoritoResponse(
        val isFavorite: Boolean
    )
    @GET("/user/myFavorites/{userId}/check/{productId}")
    fun isItemFavorito(@Path("userId") id: String, @Path("productId") itemId: String): Call<FavoritoResponse>

}