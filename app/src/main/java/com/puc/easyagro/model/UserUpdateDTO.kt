package com.puc.easyagro.model

data class UserUpdateDTO(
    val name: String,
    val nickname: String,
    val phoneNumber: String,
    val imagem: String,
    val cpf: Number
)