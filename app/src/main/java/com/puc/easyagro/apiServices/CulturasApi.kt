package com.puc.easyagro.apiServices

import com.puc.easyagro.model.Cultura

import com.puc.easyagro.model.Deficiencia
import com.puc.easyagro.model.Doenca
import com.puc.easyagro.model.NameRequestBody
import com.puc.easyagro.model.Praga
import com.puc.easyagro.model.Task
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CulturasApi {
    @GET("api/culture")
    fun getCulturas(): Call<List<Cultura>>
}

interface CulturasApiDetalhe {
    @GET("api/culture/{id}")
    fun getCulturas(@Path("id") id: String): Call<Cultura>
}

interface CulturasApiDetalheClicado {
    @GET("api/culture/{id}/info")
    fun getDoencas(@Path("id") id: String, @Query("type") type: String): Call<List<Doenca>>
    @GET("api/culture/{id}/info")
    fun getPragas(@Path("id") id: String, @Query("type") type: String): Call<List<Praga>>
    @GET("api/culture/{id}/info")
    fun getDeficiencias(@Path("id") id: String, @Query("type") type: String): Call<List<Deficiencia>>
}

interface CulturasApiItem {

    @GET("api/culture/{id}/espec")
    fun getDoenca(@Path("id") itemId: String, @Query("type") type: String, @Query("name") name: String): Call<Doenca>

    @GET("api/culture/{id}/espec")
    fun getPraga(@Path("id") itemId: String, @Query("type") type: String, @Query("name") name: String): Call<Praga>

    @GET("api/culture/{id}/espec")
    fun getDeficiencia(@Path("id") itemId: String, @Query("type") type: String, @Query("name") name: String): Call<Deficiencia>
}



