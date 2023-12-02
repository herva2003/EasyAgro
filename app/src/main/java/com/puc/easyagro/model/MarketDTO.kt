package com.puc.easyagro.model

import java.math.BigDecimal

data class MarketDTO (
    val name: String? = null,
    var userId: String,
    val price: BigDecimal? = null,
    val category: String? = null,
    val description: String? = null,
    val code: String? = null,
    val quantityInStock: Int? = null,
    val images: List<String>? = null,
)
data class VIaCepDTO(
    val cep: String?,
    val logradouro: String?,
    val bairro: String?,
    val localidade: String?,
    val uf: String?
)

data class AddressDto(
    val cep: String,
    val logradouro: String,
    val bairro: String,
    val localidade: String,
    val uf: String,
    val numero: String
)

data class UserDTO2(
    var name: String?,
    var nickname: String?,
    val login: String?,
    val password: String?,
    var phoneNumber: String?,
    val cpf: String?,
    val address: AddressDto?
)


