package com.puc.easyagro.model

class Usuario (
    var _id: String? = null,
    val login: String? = null,
    val carrinho: List<Carrinho>? = null,
    val tarefa: List<Carrinho>? = null,
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

data class Tarefa(
    val titleNotification: String,
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
    val isPermission: Boolean
)