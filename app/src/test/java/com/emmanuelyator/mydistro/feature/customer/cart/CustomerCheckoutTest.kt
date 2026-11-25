package com.emmanuelyator.mydistro.feature.customer.cart

import com.emmanuelyator.mydistro.core.common.AppError
import com.emmanuelyator.mydistro.core.common.DataResult
import com.emmanuelyator.mydistro.core.model.DeliveryAddress
import com.emmanuelyator.mydistro.core.model.Order
import com.emmanuelyator.mydistro.core.model.OrderStatus
import com.emmanuelyator.mydistro.core.model.Product
import com.emmanuelyator.mydistro.feature.customer.data.OrderItemRequest
import com.emmanuelyator.mydistro.feature.customer.data.PaymentInitiateResponse
import com.emmanuelyator.mydistro.feature.customer.data.PaymentStatusResponse
import com.emmanuelyator.mydistro.feature.customer.domain.OrderRepository
import com.emmanuelyator.mydistro.feature.driver.data.LocationPingResponse
import java.time.LocalDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CustomerCheckoutTest {

    private val address = DeliveryAddress(
        id = "addr-1",
        label = "Main Shop",
        recipientName = "Jane",
        phoneNumber = "0712345678",
        addressLine = "Moi Avenue",
        city = "Nairobi",
        isDefault = true
    )

    private fun product(id: String, org: String) = Product(
        id = id,
        name = "Cement",
        brand = "Bamburi",
        categoryId = "cat-cement",
        unitLabel = "50kg",
        priceKes = 750,
        organizationId = org
    )

    @Test
    fun `rejects a cart that mixes suppliers before calling the API`() = runBlocking {
        val repo = FakeOrderRepository()
        val checkout = CustomerCheckout(repo, pollIntervalMs = 1, pollTimeoutMs = 10, delay = {})

        val (result, _) = checkout.checkout(
            items = listOf(
                CartItemUiModel(product("p1", "org-a"), 1),
                CartItemUiModel(product("p2", "org-b"), 1)
            ),
            address = address,
            phoneNumber = "0712345678"
        )

        assertTrue(result is CheckoutResult.Failed)
        assertTrue((result as CheckoutResult.Failed).message.contains("one supplier"))
        assertEquals(0, repo.placeOrderCalls)
    }

    @Test
    fun `rejects checkout without a delivery address`() {
        val error = CustomerCheckout.validate(
            items = listOf(CartItemUiModel(product("p1", "org-a"), 1)),
            address = null,
            phoneNumber = "0712345678"
        )
        assertEquals("Choose a delivery address before paying.", error)
    }

    @Test
    fun `happy path places the order, initiates payment, and waits until confirmed`() = runBlocking {
        val repo = FakeOrderRepository(statusSequence = listOf("PENDING", "CONFIRMED"))
        val checkout = CustomerCheckout(
            orderRepository = repo,
            pollIntervalMs = 1,
            pollTimeoutMs = 1_000,
            delay = {},
            newIdempotencyKey = { "key-1" }
        )

        val (result, key) = checkout.checkout(
            items = listOf(CartItemUiModel(product("p1", "org-a"), 2)),
            address = address,
            phoneNumber = "0712345678"
        )

        assertEquals("key-1", key)
        assertTrue(result is CheckoutResult.Paid)
        assertEquals(1, repo.placeOrderCalls)
        assertEquals(1, repo.initiateCalls)
        assertEquals("key-1", repo.lastIdempotencyKey)
        assertEquals("org-a", repo.lastOrganizationId)
        assertEquals(listOf(OrderItemRequest("p1", 2)), repo.lastItems)
    }

    @Test
    fun `treats RECONCILED as paid`() = runBlocking {
        val repo = FakeOrderRepository(statusSequence = listOf("RECONCILED"))
        val checkout = CustomerCheckout(repo, pollIntervalMs = 1, pollTimeoutMs = 500, delay = {})

        val (result, _) = checkout.checkout(
            items = listOf(CartItemUiModel(product("p1", "org-a"), 1)),
            address = address,
            phoneNumber = "0712345678"
        )

        assertTrue(result is CheckoutResult.Paid)
    }

    @Test
    fun `payment failure keeps the cart responsibility with the caller and returns Failed`() = runBlocking {
        val repo = FakeOrderRepository(statusSequence = listOf("FAILED"))
        val checkout = CustomerCheckout(repo, pollIntervalMs = 1, pollTimeoutMs = 500, delay = {})

        val (result, key) = checkout.checkout(
            items = listOf(CartItemUiModel(product("p1", "org-a"), 1)),
            address = address,
            phoneNumber = "0712345678",
            idempotencyKey = "retry-key"
        )

        assertEquals("retry-key", key)
        assertTrue(result is CheckoutResult.Failed)
        assertEquals("retry-key", repo.lastIdempotencyKey)
    }

    @Test
    fun `reuses the provided idempotency key on retry`() = runBlocking {
        val repo = FakeOrderRepository(statusSequence = listOf("CONFIRMED"))
        val checkout = CustomerCheckout(
            orderRepository = repo,
            pollIntervalMs = 1,
            pollTimeoutMs = 500,
            delay = {},
            newIdempotencyKey = { "should-not-use" }
        )

        val (_, key) = checkout.checkout(
            items = listOf(CartItemUiModel(product("p1", "org-a"), 1)),
            address = address,
            phoneNumber = "0712345678",
            idempotencyKey = "same-attempt"
        )

        assertEquals("same-attempt", key)
        assertEquals("same-attempt", repo.lastIdempotencyKey)
    }

    @Test
    fun `paymentStatusLabel maps RECONCILED to Paid`() {
        assertEquals("Paid", CustomerCheckout.paymentStatusLabel("RECONCILED"))
        assertEquals("Paid", CustomerCheckout.paymentStatusLabel("CONFIRMED"))
        assertEquals("Failed", CustomerCheckout.paymentStatusLabel("FAILED"))
    }

    private class FakeOrderRepository(
        private val statusSequence: List<String> = listOf("CONFIRMED")
    ) : OrderRepository {
        var placeOrderCalls = 0
        var initiateCalls = 0
        var lastIdempotencyKey: String? = null
        var lastOrganizationId: String? = null
        var lastItems: List<OrderItemRequest> = emptyList()
        private var statusIndex = 0

        override fun observeOrders(): Flow<List<Order>> = flowOf(emptyList())
        override suspend fun refreshOrders(): DataResult<Unit> = DataResult.Success(Unit)
        override suspend fun getOrder(orderId: String): DataResult<Order> =
            DataResult.Failure(AppError.Network("unused"))

        override suspend fun placeOrder(
            organizationId: String,
            address: DeliveryAddress,
            items: List<OrderItemRequest>
        ): DataResult<Order> {
            placeOrderCalls++
            lastOrganizationId = organizationId
            lastItems = items
            return DataResult.Success(
                Order(
                    id = "order-1",
                    status = OrderStatus.PAYMENT_PENDING,
                    itemSummary = "items",
                    totalKes = 1_500,
                    date = LocalDateTime.now(),
                    orderNumber = "ORD-1"
                )
            )
        }

        override suspend fun initiatePayment(
            orderId: String,
            phoneNumber: String,
            idempotencyKey: String
        ): DataResult<PaymentInitiateResponse> {
            initiateCalls++
            lastIdempotencyKey = idempotencyKey
            return DataResult.Success(
                PaymentInitiateResponse(
                    paymentId = "pay-1",
                    status = "PENDING",
                    customerMessage = "STK sent"
                )
            )
        }

        override suspend fun getPaymentStatus(orderId: String): DataResult<PaymentStatusResponse> {
            val status = statusSequence.getOrElse(statusIndex) { statusSequence.last() }
            statusIndex++
            return DataResult.Success(
                PaymentStatusResponse(orderId = orderId, status = status, failureReason = "declined")
            )
        }

        override suspend fun getOrderPayments(orderId: String) =
            DataResult.Failure(AppError.Network("unused"))

        override suspend fun cancelOrder(orderId: String, note: String?) =
            DataResult.Failure(AppError.Network("unused"))

        override suspend fun requestDeliveryOtp(orderId: String) = DataResult.Success(Unit)
        override suspend fun requestStopDeliveryOtp(tripId: String, stopId: String) =
            DataResult.Success(Unit)

        override suspend fun findTripStopForOrder(order: Order): Pair<String, String>? = null
        override suspend fun getTripTracking(tripId: String): List<LocationPingResponse> = emptyList()
    }
}
