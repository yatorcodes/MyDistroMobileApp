package com.emmanuelyator.mydistro.core.model

/**
 * Conceptual order lifecycle shared with the Spring Boot backend and the Angular
 * admin console. The happy path runs SUBMITTED through CLOSED; the remaining
 * entries are terminal or exceptional states that can interrupt it.
 *
 * Keep this in sync with the backend enum — it is the contract, not a UI concern.
 */
enum class OrderStatus {
    SUBMITTED,
    CONFIRMED,
    PAYMENT_PENDING,
    PAID,
    ASSIGNED,
    DISPATCHED,
    IN_TRANSIT,
    DELIVERED,
    CLOSED,

    DELAYED,
    CANCELLED,
    PAYMENT_FAILED,
    DELIVERY_FAILED,
    DISPUTED;

    val isTerminal: Boolean
        get() = this in setOf(CLOSED, CANCELLED, DELIVERY_FAILED, DISPUTED)
}

/** The ordered happy-path states, used to render lifecycle timelines. */
val OrderStatusTimeline: List<OrderStatus> = listOf(
    OrderStatus.SUBMITTED,
    OrderStatus.CONFIRMED,
    OrderStatus.PAYMENT_PENDING,
    OrderStatus.PAID,
    OrderStatus.ASSIGNED,
    OrderStatus.DISPATCHED,
    OrderStatus.IN_TRANSIT,
    OrderStatus.DELIVERED,
    OrderStatus.CLOSED
)
