package com.puc.easyagro.apiServices

import com.puc.easyagro.model.CepeaProducts
import com.puc.easyagro.model.ResponseBody
import com.puc.easyagro.model.StatusResponse
import retrofit2.Call
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
    @GET("cotacoes/statusCot")
    suspend fun checkStatusCot(): Response<StatusResponse>

    @POST("cotacoes/statusCot")
    suspend fun updateStatusCot(): Response<StatusResponse>

    @POST("cotacoes")
    suspend fun sendDataToMongoDB(@Body data: Any): Response<Unit>

    @GET("api/cepea/products")
    suspend fun getProductsCepea(): Response<CepeaProducts>
}