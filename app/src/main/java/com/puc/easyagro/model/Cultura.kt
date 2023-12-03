package com.puc.easyagro.model

data class Cultura(
    val _id: String? = null,
    val nome: String? = null,
    val imagem: String? = null,
    val doencas: List<Unit>? = null,
    val pragas: List<Unit>? = null,
    val deficiencias: List<Unit>? = null
)

data class Doenca(
    val nome: String? = null,
    val agenteCausal: String? = null,
    val descricao: String? = null,
    val disseminacao: String? = null,
    val condicoesFavoraveis: String? = null,
    val controle: String? = null,
    val imagem: String? = null,
)

data class Praga(
    val nome: String? = null,
    val nomeCientifico: String? = null,
    val descricao: String? = null,
    val sintomasDanos: String? = null,
    val disseminacao: String? = null,
    val condicoesFavoraveis: String? = null,
    val controle: String? = null,
    val imagem: String? = null,
)

data class Deficiencia(
    val nome: String? = null,
    val sintomas: String? = null,
    val imagem: String? = null,
)
