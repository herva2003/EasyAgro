package com.puc.easyagro.ui.market.viewItemMarket

import io.opencensus.common.ServerStatsFieldEnums.Id
import org.bson.types.ObjectId


data class Review (
    val reviewId: ObjectId? = null,
    val code: String? = null,
    val comment: String? = null,
    val ratting: Double = 0.0
)