package com.puc.easyagro.apiServices

import com.puc.easyagro.model.Market
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MarketApi {

//    @GET("api/products")
//    fun getItemsMarket(): Call<List<Market>>

    @GET("/mercado/games/")
    fun getItemsMarket(): Call<List<Market>>

//    @POST("api/products/create")
//    fun addProduct(@Body produto: Market): Call<ResponseBody>

    @POST("/mercado/games/")
    fun addProduct(@Body produto: Market): Call<ResponseBody>
}

interface MarketApiDetalhe {
    @GET("/mercado/games/{id}")
    fun getItem(@Path("id") id: String): Call<Market>
}
