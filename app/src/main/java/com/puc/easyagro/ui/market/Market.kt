package com.puc.easyagro.ui.market

import com.puc.easyagro.ui.market.viewItemMarket.Review
import java.math.BigDecimal
import java.time.Instant

data class Market(
    var _id: String? = null,
    val name: String? = null,
    val price: BigDecimal? = null,
    val category: String? = null,
    val description: String? = null,
    val code: String? = null,
    val quantityInStock: Int? = null,
    val createdAt: String? = null,
    val images: List<String>? = null,
    val reviewIds: List<Review>? = null
)
