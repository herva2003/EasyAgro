package com.puc.easyagro.apiServices

import com.puc.easyagro.model.Cotacao
import com.puc.easyagro.model.ResponseBody
import com.puc.easyagro.model.StatusResponse
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
    @GET("cotacoes/games/statusCot")
    suspend fun checkStatusCot(): Response<StatusResponse>

    @POST("cotacoes/games/statusCot")
    suspend fun updateStatusCot(): Response<StatusResponse>

    @POST("cotacoes/games")
    suspend fun sendDataToMongoDB(@Body data: Any): Response<Unit>

    @GET("cotacoes/games/produtos")
    suspend fun fetchDataFromMongoDB(): Response<List<Cotacao>>
}