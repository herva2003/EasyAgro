package com.puc.easyagro.apiServices

import com.puc.easyagro.model.Cultura
import com.puc.easyagro.model.Deficiencia
import com.puc.easyagro.model.Doenca
import com.puc.easyagro.model.Praga
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CulturasApi {
    @GET("cultura/")
    fun getCulturas(): Call<List<Cultura>>
}

interface CulturasApiDetalhe {
    @GET("/cultura/{id}")
    fun getCulturas(@Path("id") id: String): Call<Cultura>
}

interface CulturasApiDetalheClicado {
    @GET("cultura/{itemId}/info")
    fun getDoencas(@Path("itemId") itemId: String, @Query("type") type: String): Call<List<Doenca>>
    @GET("cultura/{itemId}/info")
    fun getPragas(@Path("itemId") itemId: String, @Query("type") type: String): Call<List<Praga>>
    @GET("cultura/{itemId}/info")
    fun getDeficiencias(@Path("itemId") itemId: String, @Query("type") type: String): Call<List<Deficiencia>>
}

interface CulturasApiItem {
    @GET("cultura/{itemId}/info/{item}")
    fun getDoenca(@Path("itemId") itemId: String, @Path("item") item: String, @Query("type") type: String): Call<Doenca>
    @GET("cultura/{itemId}/info/{item}")
    fun getPraga(@Path("itemId") itemId: String, @Path("item") item: String, @Query("type") type: String): Call<Praga>
    @GET("cultura/{itemId}/info/{item}")
    fun getDeficiencia(@Path("itemId") itemId: String, @Path("item") item: String, @Query("type") type: String): Call<Deficiencia>
}


