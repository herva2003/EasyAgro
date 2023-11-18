package com.puc.easyagro.model

data class UserUpdateDTO(
    val name: String,
    val nickname: String,
    val phoneNumber: String,
    val cpf: Number
)