package com.puc.easyagro.ui.home.culturas

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CulturasApi {
    @GET("http://192.168.0.243:8080/games/")
    fun getCulturas(): Call<List<Cultura>>
}

interface CulturasApiDetalhe {
    @GET("/games/{id}")
    fun getCulturas(@Path("id") id: String): Call<Cultura>
}

interface CulturasApiDetalheClicado {
    @GET("games/{itemId}/info")
    fun getDoencas(@Path("itemId") itemId: String, @Query("type") type: String): Call<List<Doenca>>

    @GET("games/{itemId}/info")
    fun getPragas(@Path("itemId") itemId: String, @Query("type") type: String): Call<List<Praga>>

    @GET("games/{itemId}/info")
    fun getDeficiencias(@Path("itemId") itemId: String, @Query("type") type: String): Call<List<Deficiencia>>
}

interface CulturasApiItem {
    @GET("games/{itemId}/info/{item}")
    fun getDoenca(@Path("itemId") itemId: String, @Path("item") item: String, @Query("type") type: String): Call<Doenca>
    @GET("games/{itemId}/info/{item}")
    fun getPraga(@Path("itemId") itemId: String, @Path("item") item: String, @Query("type") type: String): Call<Praga>
    @GET("games/{itemId}/info/{item}")
    fun getDeficiencia(@Path("itemId") itemId: String, @Path("item") item: String, @Query("type") type: String): Call<Deficiencia>
}


