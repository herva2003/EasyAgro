package com.puc.easyagro.apiServices

import com.google.gson.JsonObject
import com.puc.easyagro.model.Market
import com.puc.easyagro.model.MarketDTO
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MarketApi {

    @GET("api/products")
    fun getItemsMarket(): Call<List<Market>>

//    @GET("/mercado/")
//    fun getItemsMarket(): Call<List<Market>>

    @POST("api/products/create")
    fun addProduct(@Body produto: MarketDTO): Call<ResponseBody>

//    @POST("/mercado/")
//    fun addProduct(@Body produto: Market): Call<ResponseBody>
}

interface MarketApiDetalhe {
    @GET("/mercado/{id}")
    fun getItem(@Path("id") id: String): Call<Market>
}

interface CarrinhoApi {
    @POST("/mercado/carrinho")
    fun addItem(@Body itemUser: JsonObject): Call<Void>
}

