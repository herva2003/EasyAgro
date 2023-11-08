package com.puc.easyagro.apiServices

import com.puc.easyagro.model.Cotacao
import com.puc.easyagro.model.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface CotacaoApiBasic {
    @FormUrlEncoded
    @POST("consultas/esalq/cepea")
    suspend fun getCotacao(
        @Field("token") token: String, @Field("timeout") timeout: String): ResponseBody
}

interface CotacaoApi {
    @GET("checkMongoDB")
    suspend fun checkMongoDB(): Response<Boolean>

    @POST("cotacoes/games")
    suspend fun sendDataToMongoDB(@Body data: Any): Response<Unit>

    @GET("cotacoes/games/produtos")
    suspend fun fetchDataFromMongoDB(): Response<List<Cotacao>>
}

interface BackendService {
    @GET("fetchDataFromMongoDB")
    suspend fun fetchDataFromMongoDB(): Response<Cotacao>
}