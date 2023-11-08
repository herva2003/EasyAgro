package com.puc.easyagro.model

data class Produto (
    var data: String? = null,
    var produto: String? = null,
    var valor: String? = null,
    var nome: String? = null,
    var local: String? = null,
    var unidade: String? = null,
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
