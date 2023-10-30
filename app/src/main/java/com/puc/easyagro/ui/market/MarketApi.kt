package com.puc.easyagro.ui.market

import com.puc.easyagro.ui.home.culturas.Cultura
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MarketApi {
    @GET("mercado/games/")
    fun getItemsMarket(): Call<List<Market>>

    @POST("mercado/games/")
    fun addProduct(@Body produto: Market): Call<ResponseBody>
}

interface MarketApiDetalhe {
    @GET("/mercado/games/{id}")
    fun getItem(@Path("id") id: String): Call<Market>
}
