package com.puc.easyagro.apiServices

import com.google.gson.JsonObject
import com.puc.easyagro.model.Market
import com.puc.easyagro.model.MarketDTO
import com.puc.easyagro.model.Task
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MarketApi {

    @GET("api/products")
    fun getItemsMarket(): Call<List<Market>>

//    @GET("/mercado/")
//    fun getItemsMarket(): Call<List<Market>>

    @POST("api/products/create")
    fun addProduct(@Body produto: MarketDTO): Call<ResponseBody>

//    @POST("/mercado/")
//    fun addProduct(@Body produto: MarketDTO): Call<MarketDTO>
}

interface MarketApiDetalhe {
    @GET("api/products/single/{productId}")
    fun getItem(@Path("productId") id: String): Call<Market>
}

interface CarrinhoApi {
    @POST("user/cart/add")
    fun addItemCarrinnho(@Body itemUser: JsonObject): Call<Void>

    @POST("user/favorite/add")
    fun addItemFavoritos(@Body itemUser: JsonObject): Call<Void>

    @DELETE("user/myFavorites/user/{userId}/remove/{productId}")
    fun removeItemFavoritos(
        @Path("userId") userId: String,
        @Path("productId") productId: String
    ): Call<Void>
}

