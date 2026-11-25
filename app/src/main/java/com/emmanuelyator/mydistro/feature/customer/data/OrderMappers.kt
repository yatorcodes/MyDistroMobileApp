package com.emmanuelyator.mydistro.feature.customer.data

import com.emmanuelyator.mydistro.core.model.Order
import com.emmanuelyator.mydistro.core.model.OrderStatus
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException

fun OrderResponseDto.toOrder(): Order {
    val summary = when {
        !items.isNullOrEmpty() -> {
            val count = items.sumOf { it.quantity ?: 0 }
            if (count <= 1) "${count} item" else "$count items"
        }
        else -> orderNumber ?: "Order"
    }
    return Order(
        id = id,
        status = mapOrderStatus(status),
        itemSummary = summary,
        totalKes = totalAmount?.toInt() ?: 0,
        date = parseBackendTime(placedAt) ?: LocalDateTime.now(),
        orderNumber = orderNumber
    )
}

fun mapOrderStatus(raw: String?): OrderStatus = when (raw?.uppercase()) {
    "PENDING" -> OrderStatus.PAYMENT_PENDING
    "CONFIRMED" -> OrderStatus.CONFIRMED
    "PROCESSING" -> OrderStatus.PAID
    "AWAITING_DISPATCH" -> OrderStatus.DISPATCHED
    "IN_TRANSIT" -> OrderStatus.IN_TRANSIT
    "DELIVERED" -> OrderStatus.DELIVERED
    "CANCELLED" -> OrderStatus.CANCELLED
    "REFUNDED" -> OrderStatus.CLOSED
    "FAILED" -> OrderStatus.PAYMENT_FAILED
    else -> OrderStatus.SUBMITTED
}

fun parseBackendTime(raw: String?): LocalDateTime? {
    if (raw.isNullOrBlank()) return null
    return try {
        OffsetDateTime.parse(raw).toLocalDateTime()
    } catch (_: DateTimeParseException) {
        try {
            LocalDateTime.parse(raw)
        } catch (_: DateTimeParseException) {
            null
        }
    }
}
