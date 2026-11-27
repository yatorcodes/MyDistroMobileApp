package com.emmanuelyator.mydistro.feature.customer.data

import com.emmanuelyator.mydistro.core.common.AppError
import com.emmanuelyator.mydistro.core.common.DataResult
import com.emmanuelyator.mydistro.core.model.DeliveryAddress
import com.emmanuelyator.mydistro.core.model.Order
import com.emmanuelyator.mydistro.core.model.OrderStatus
import com.emmanuelyator.mydistro.feature.customer.domain.OrderRepository
import com.emmanuelyator.mydistro.feature.driver.data.LocationPingResponse
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime

@Singleton
class MockOrderRepository @Inject constructor() : OrderRepository {

    private val orders = MutableStateFlow(
        MockOrderData.orders + Order(
            id = "ORD-4601",
            status = OrderStatus.PAYMENT_PENDING,
            itemSummary = "Bamburi Nguvu (10 bags)",
            totalKes = 7_500,
            date = LocalDateTime.now().minusHours(2),
            orderNumber = "ORD-4601"
        )
    )

    override fun observeOrders(): Flow<List<Order>> = orders.asStateFlow()

    override suspend fun refreshOrders(): DataResult<Unit> {
        delay(400)
        return DataResult.Success(Unit)
    }

    override suspend fun getOrder(orderId: String): DataResult<Order> {
        delay(300)
        val order = orders.value.firstOrNull { it.id == orderId }
        return if (order != null) DataResult.Success(order)
        else DataResult.Failure(AppError.Network("Order not found"))
    }

    // Presentation mode: a payment "completes" a couple of seconds after the prompt is requested.
    private val paymentRequestedAt = mutableMapOf<String, Long>()

    override suspend fun placeOrder(
        organizationId: String,
        address: DeliveryAddress,
        items: List<OrderItemRequest>
    ): DataResult<Order> {
        delay(500)
        val number = "ORD-${4700 + orders.value.size}"
        val order = Order(
            id = number,
            status = OrderStatus.PAYMENT_PENDING,
            itemSummary = "${items.sumOf { it.quantity }} items to ${address.label}",
            totalKes = 0,
            date = LocalDateTime.now(),
            orderNumber = number
        )
        orders.value = listOf(order) + orders.value
        return DataResult.Success(order)
    }

    override suspend fun initiatePayment(
        orderId: String,
        provider: com.emmanuelyator.mydistro.feature.customer.domain.PaymentProvider,
        phoneNumber: String?,
        idempotencyKey: String
    ): DataResult<PaymentInitiateResponse> {
        delay(600)
        paymentRequestedAt.putIfAbsent(orderId, System.currentTimeMillis())
        val isPaystack = provider == com.emmanuelyator.mydistro.feature.customer.domain.PaymentProvider.PAYSTACK
        return DataResult.Success(
            PaymentInitiateResponse(
                paymentId = "pay-mock-$orderId",
                mpesaCheckoutRequestId = if (isPaystack) null else "ws_CO_MOCK_$orderId",
                status = "PENDING",
                customerMessage = if (isPaystack) {
                    "DEMO — NO MONEY TRANSFERRED. Open the sandbox checkout URL."
                } else {
                    "Success. Request accepted for processing"
                },
                provider = provider.apiValue,
                authorizationUrl = if (isPaystack) "https://checkout.paystack.com/mock-$orderId" else null,
                providerMode = "TEST",
                paymentReference = if (isPaystack) "mock-ref-$orderId" else null
            )
        )
    }

    override suspend fun getPaymentStatus(orderId: String): DataResult<PaymentStatusResponse> {
        delay(200)
        val requestedAt = paymentRequestedAt[orderId]
        if (requestedAt != null && System.currentTimeMillis() - requestedAt >= MOCK_PAYMENT_DELAY_MS) {
            orders.value = orders.value.map {
                if (it.id == orderId && it.status == OrderStatus.PAYMENT_PENDING) {
                    it.copy(status = OrderStatus.PAID)
                } else it
            }
        }
        val order = orders.value.firstOrNull { it.id == orderId }
        val status = when (order?.status) {
            OrderStatus.PAYMENT_PENDING -> "PENDING"
            OrderStatus.PAYMENT_FAILED -> "FAILED"
            else -> "CONFIRMED"
        }
        return DataResult.Success(
            PaymentStatusResponse(
                orderId = orderId,
                paymentId = "pay-mock-$orderId",
                status = status,
                amount = order?.totalKes?.toDouble(),
                mpesaReceiptNumber = if (status == "CONFIRMED") "QK7MOCK123" else null,
                provider = "DARAJA",
                providerMode = "TEST"
            )
        )
    }

    override suspend fun getOrderPayments(orderId: String): DataResult<PaymentResponseDto> {
        delay(200)
        val order = orders.value.firstOrNull { it.id == orderId }
        return DataResult.Success(
            PaymentResponseDto(
                id = "pay-mock-$orderId",
                orderId = orderId,
                amount = order?.totalKes?.toDouble(),
                method = "MPESA",
                status = if (order?.status == OrderStatus.PAYMENT_PENDING) "PENDING" else "CONFIRMED",
                mpesaReceiptNumber = "QK7MOCK123"
            )
        )
    }

    override suspend fun cancelOrder(orderId: String, note: String?): DataResult<Order> {
        delay(500)
        val current = orders.value.firstOrNull { it.id == orderId }
            ?: return DataResult.Failure(AppError.Network("Order not found"))
        val updated = current.copy(status = OrderStatus.CANCELLED)
        orders.value = orders.value.map { if (it.id == orderId) updated else it }
        return DataResult.Success(updated)
    }

    override suspend fun requestDeliveryOtp(orderId: String): DataResult<Unit> {
        delay(500)
        return DataResult.Success(Unit)
    }

    override suspend fun requestStopDeliveryOtp(tripId: String, stopId: String): DataResult<Unit> {
        delay(500)
        return DataResult.Success(Unit)
    }

    override suspend fun findTripStopForOrder(order: Order): Pair<String, String>? {
        if (order.status != OrderStatus.IN_TRANSIT) return null
        return "trip-1042" to "stop-3"
    }

    override suspend fun getTripTracking(tripId: String): List<LocationPingResponse> =
        listOf(
            LocationPingResponse(
                id = "ping-1",
                lat = -1.2921,
                lng = 36.8219,
                recordedAt = LocalDateTime.now().toString()
            )
        )

    private companion object {
        const val MOCK_PAYMENT_DELAY_MS = 2_500L
    }
}
