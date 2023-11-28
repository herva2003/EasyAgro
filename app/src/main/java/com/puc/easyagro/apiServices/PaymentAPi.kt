package com.puc.easyagro.apiServices

import com.puc.easyagro.model.PixPaymentDTO
import com.puc.easyagro.model.PixPaymentResponseDTO

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentAPi {
    @POST("process_payment")
    fun buyProducts(@Body pixPaymentDTO: PixPaymentDTO): Call<PixPaymentResponseDTO>
}