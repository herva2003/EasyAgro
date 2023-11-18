package com.puc.easyagro.model

class Usuario (
    var _id: String? = null,
    val login: String? = null,
    val myCart: List<Cart>? = null,
    val myAdverts: List<MyProducts>? = null,
    val myShopping: List<MyShopping>? = null,
    val myFavorites: List<Favorites>? = null,
    val tarefa: List<Task>? = null,
    val password: String? = null,
    val nickname: String? = null,
    val name: String? = null,
    val phoneNumber: String? = null,
    val cpf: Number? = null,
    val endereco: String? = null,
    val _class: String? = null
)

data class Cart(
    var _id: String? = null,
)

data class Task(
    val title: String,
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
    val isPermission: Boolean
)

data class MyProducts(
    var _id: String? = null,
)

data class MyShopping(
    var _id: String? = null,
)

data class Favorites(
    var _id: String? = null,
)