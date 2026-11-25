package com.emmanuelyator.mydistro.core.model

import java.time.LocalDateTime

data class Order(
    val id: String,
    val status: OrderStatus,
    val itemSummary: String,
    val totalKes: Int,
    val date: LocalDateTime,
    val orderNumber: String? = null
) {
    val totalLabel: String get() = "KSh %,d".format(totalKes)
    val displayId: String get() = orderNumber ?: id.take(8)
}
