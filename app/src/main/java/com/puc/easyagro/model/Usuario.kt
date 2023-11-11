package com.puc.easyagro.model

class Usuario (
    var _id: String? = null,
    val login: String? = null,
    val carrinho: List<Carrinho>? = null,
    val password: String? = null,
    val userName: String? = null,
    val nome: String? = null,
    val phoneNumber: String? = null,
    val cpf: String? = null,
    val endereco: String? = null,
    val _class: String? = null
)

data class Carrinho(
    var _id: String? = null,
)