package com.puc.easyagro.ui.market

import java.math.BigDecimal
import java.time.Instant

data class Market(
    var _id: String? = null,
    val nome: String? = null,
    val preco: Double? = null,
    val categoria: String? = null,
    val descricao: String? = null,
)

