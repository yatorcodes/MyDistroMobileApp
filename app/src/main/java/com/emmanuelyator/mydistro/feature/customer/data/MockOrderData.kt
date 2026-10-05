package com.emmanuelyator.mydistro.feature.customer.data

import com.emmanuelyator.mydistro.core.model.Order
import com.emmanuelyator.mydistro.core.model.OrderStatus
import java.time.LocalDateTime

object MockOrderData {

    val orders = listOf(
        Order(
            id = "ORD-4587",
            status = OrderStatus.DELIVERED,
            itemSummary = "Bamburi + Simba Cement",
            totalKes = 1760,
            date = LocalDateTime.of(2025, 9, 15, 10, 24)
        ),
        Order(
            id = "ORD-4520",
            status = OrderStatus.DELIVERED,
            itemSummary = "Steel Rods",
            totalKes = 12500,
            date = LocalDateTime.of(2025, 9, 10, 14, 30)
        ),
        Order(
            id = "ORD-4481",
            status = OrderStatus.DELIVERED,
            itemSummary = "Paint (3 items)",
            totalKes = 4200,
            date = LocalDateTime.of(2025, 9, 2, 9, 15)
        ),
        Order(
            id = "ORD-4410",
            status = OrderStatus.IN_TRANSIT,
            itemSummary = "Cement (Bamburi)",
            totalKes = 1160,
            date = LocalDateTime.of(2025, 8, 28, 16, 45)
        )
    )
}
