package com.puc.easyagro.model

import com.google.maps.model.PlaceDetails
import java.math.BigDecimal

data class Market(
    var id: String? = null,
    val userId: String? = null,
    val name: String? = null,
    val price: BigDecimal? = null,
    val category: String? = null,
    val description: String? = null,
    val code: String? = null,
    val quantityInStock: Int? = null,
    val createdAt: String? = null,
    val images: List<String>? = null,
    val reviewIds: List<PlaceDetails.Review>? = null
)