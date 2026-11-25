package com.emmanuelyator.mydistro.feature.customer.domain

import com.emmanuelyator.mydistro.core.common.DataResult
import com.emmanuelyator.mydistro.core.model.Order
import com.emmanuelyator.mydistro.core.model.DeliveryAddress
import com.emmanuelyator.mydistro.feature.customer.data.OrderItemRequest
import com.emmanuelyator.mydistro.feature.customer.data.PaymentInitiateResponse
import com.emmanuelyator.mydistro.feature.customer.data.PaymentStatusResponse
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun observeOrders(): Flow<List<Order>>
    suspend fun refreshOrders(): DataResult<Unit>

    /**
     * Places an order with one distributor. The order is created unpaid: stock is
     * reserved for a limited time and nothing is dispatched until payment is confirmed.
     */
    suspend fun placeOrder(
        organizationId: String,
        address: DeliveryAddress,
        items: List<OrderItemRequest>
    ): DataResult<Order>

    /** Asks M-Pesa to send the payment prompt to [phoneNumber]. Safe to repeat with the same key. */
    suspend fun initiatePayment(
        orderId: String,
        phoneNumber: String,
        idempotencyKey: String
    ): DataResult<PaymentInitiateResponse>
    suspend fun getOrder(orderId: String): DataResult<Order>
    suspend fun getPaymentStatus(orderId: String): DataResult<PaymentStatusResponse>
    suspend fun getOrderPayments(orderId: String): DataResult<com.emmanuelyator.mydistro.feature.customer.data.PaymentResponseDto>
    suspend fun cancelOrder(orderId: String, note: String? = null): DataResult<Order>
    suspend fun requestDeliveryOtp(orderId: String): DataResult<Unit>
    suspend fun requestStopDeliveryOtp(tripId: String, stopId: String): DataResult<Unit>
    /** Returns tripId to stopId when a live trip contains this order. */
    suspend fun findTripStopForOrder(order: Order): Pair<String, String>?
    suspend fun getTripTracking(tripId: String): List<com.emmanuelyator.mydistro.feature.driver.data.LocationPingResponse>
}
