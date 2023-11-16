package com.puc.easyagro.model

import com.google.maps.model.PlaceDetails
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