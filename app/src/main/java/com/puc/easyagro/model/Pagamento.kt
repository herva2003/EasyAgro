package com.puc.easyagro.model

import java.math.BigDecimal

data class PixPaymentDTO(
    val transactionAmount: BigDecimal,
    val productDescription: String,
    val payer: PayerDTO,
    val orders: List<ProdutosPix>?
)

data class ProdutosPix(
    var productId: String? = null,
    var quantity: Int? = null,
    var buyerId: String? = null,
    var sellerId: String? = null,
    var price: BigDecimal? = null,
)

data class PayerDTO(
    val firstName: String,
    val lastName: String,
    val email: String,
    val identification: PayerIdentificationDTO
)

data class PayerIdentificationDTO(
    val type: String,
    val number: String
)

data class PixPaymentResponseDTO(
    val id: Long,
    val status: String,
    val detail: String,
    val qrCodeBase64: String,
    val qrCode: String
)