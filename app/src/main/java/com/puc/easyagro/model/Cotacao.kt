package com.puc.easyagro.model

import java.time.Instant

data class Produto (
    var data: String? = null,
    var produto: String? = null,
    var valor: String? = null,
    var nome: String? = null,
    var local: String? = null,
    var unidade: String? = null,
)

data class CepeaProducts (
    val id: String,
    val updateAt: Instant,
    val products: List<ProductCepeaDTO>
)

data class ProductCepeaDTO(
    val data: String,
    val produto: String,
    val valor: String,
    val nome: String,
    val local: String,
    val unidade: String
)


data class Cotacao (
    var produtos: List<Produto>? = null
)

data class ResponseBody(
    val code: Int,
    val code_message: String,
    val data: Any,
    val header: Any,
    val site_receipts: List<String>,
    val errors: List<String>?
)

data class StatusResponse(
    val _id: String,
    val statusCot: Boolean
)
