package com.puc.easyagro.model

import java.math.BigDecimal

data class Pagamento (
    var id: String? = null,
    var transitionId: String? = null,
    var totalPrice: String? = null,
    var createdAt: String? = null,
    var status: String? = null,
    var produtos: List<Produtos>? = null
)

data class Produtos(
    var productId: String? = null,
    var quantity: Int? = null,
    var buyerId: String? = null,
    var sellerId: String? = null,
    var price: BigDecimal? = null,
)


