package com.emmanuelyator.mydistro.feature.customer.cart

import com.emmanuelyator.mydistro.core.common.AppError
import com.emmanuelyator.mydistro.core.common.DataResult
import com.emmanuelyator.mydistro.core.model.DeliveryAddress
import com.emmanuelyator.mydistro.feature.customer.data.OrderItemRequest
import com.emmanuelyator.mydistro.feature.customer.domain.OrderRepository
import java.util.UUID

/** Outcome of placing an order and waiting for the M-Pesa prompt to settle. */
sealed interface CheckoutResult {
    data class Paid(val orderId: String, val message: String) : CheckoutResult
    data class Failed(val message: String) : CheckoutResult
}

/**
 * Places one unpaid order for a single supplier, sends the STK prompt, and polls
 * until the payment is confirmed (or the window runs out). The cart is cleared
 * only after [CheckoutResult.Paid] so a failed or abandoned prompt keeps the items.
 */
class CustomerCheckout(
    private val orderRepository: OrderRepository,
    private val pollIntervalMs: Long = 2_000L,
    private val pollTimeoutMs: Long = 90_000L,
    private val delay: suspend (Long) -> Unit = { kotlinx.coroutines.delay(it) },
    private val newIdempotencyKey: () -> String = { UUID.randomUUID().toString() }
) {

    /**
     * [idempotencyKey] is reused when the same payment attempt is retried (for
     * example after a network blip before the prompt was accepted). Pass null to
     * mint a new key for a fresh attempt.
     */
    suspend fun checkout(
        items: List<CartItemUiModel>,
        address: DeliveryAddress?,
        phoneNumber: String,
        idempotencyKey: String? = null
    ): Pair<CheckoutResult, String> {
        val key = idempotencyKey ?: newIdempotencyKey()
        val validationError = validate(items, address, phoneNumber)
        if (validationError != null) {
            return CheckoutResult.Failed(validationError) to key
        }
        val organizationId = items.map { it.product.organizationId.trim() }.distinct().single()
        val orderItems = items.map {
            OrderItemRequest(stockItemId = it.product.id, quantity = it.quantity)
        }

        when (val placed = orderRepository.placeOrder(organizationId, address!!, orderItems)) {
            is DataResult.Failure -> return CheckoutResult.Failed(placed.error.message) to key
            is DataResult.Success -> {
                val orderId = placed.data.id
                when (
                    val initiated = orderRepository.initiatePayment(
                        orderId = orderId,
                        phoneNumber = phoneNumber.trim(),
                        idempotencyKey = key
                    )
                ) {
                    is DataResult.Failure ->
                        return CheckoutResult.Failed(initiated.error.message) to key
                    is DataResult.Success -> {
                        val promptMessage = initiated.data.customerMessage
                            ?.takeIf { it.isNotBlank() }
                            ?: "Payment prompt sent. Complete M-Pesa on your phone."
                        val paid = awaitPayment(orderId)
                        return when (paid) {
                            is DataResult.Success ->
                                CheckoutResult.Paid(
                                    orderId = orderId,
                                    message = "$promptMessage Order ${placed.data.orderNumber ?: orderId} is paid."
                                ) to key
                            is DataResult.Failure ->
                                CheckoutResult.Failed(paid.error.message) to key
                        }
                    }
                }
            }
        }
    }

    private suspend fun awaitPayment(orderId: String): DataResult<Unit> {
        val deadline = System.currentTimeMillis() + pollTimeoutMs
        var lastFailure: String? = null
        while (System.currentTimeMillis() < deadline) {
            when (val status = orderRepository.getPaymentStatus(orderId)) {
                is DataResult.Success -> {
                    when (status.data.status?.uppercase()) {
                        "CONFIRMED", "RECONCILED" -> return DataResult.Success(Unit)
                        "FAILED", "REVERSED" -> {
                            val reason = status.data.failureReason?.takeIf { it.isNotBlank() }
                                ?: "Payment was not completed. You can try again."
                            return DataResult.Failure(AppError.Validation(reason))
                        }
                        else -> lastFailure = status.data.failureReason
                    }
                }
                is DataResult.Failure -> lastFailure = status.error.message
            }
            delay(pollIntervalMs)
        }
        return DataResult.Failure(
            AppError.Validation(
                lastFailure?.takeIf { it.isNotBlank() }
                    ?: "Still waiting for M-Pesa. Complete the prompt on your phone, then open the order to check status. Your cart was kept."
            )
        )
    }

    companion object {
        fun validate(
            items: List<CartItemUiModel>,
            address: DeliveryAddress?,
            phoneNumber: String
        ): String? {
            if (items.isEmpty()) return "Cart is empty"
            if (address == null) return "Choose a delivery address before paying."
            if (address.summary.isBlank()) return "The selected delivery address is incomplete."
            val phone = phoneNumber.trim()
            if (phone.length < 10) return "Enter a valid M-Pesa phone number."
            val orgIds = items.map { it.product.organizationId.trim() }.filter { it.isNotEmpty() }.distinct()
            if (orgIds.isEmpty()) {
                return "These products are missing a supplier. Refresh the catalogue and try again."
            }
            if (orgIds.size > 1) {
                return "Checkout is for one supplier at a time. Remove items from the other suppliers, then pay."
            }
            return null
        }

        fun paymentStatusLabel(status: String?): String = when (status?.uppercase()) {
            "CONFIRMED", "RECONCILED" -> "Paid"
            "PENDING", "INITIATING" -> "Pending"
            "FAILED" -> "Failed"
            "REVERSED" -> "Reversed"
            null, "" -> "Unknown"
            else -> status
        }
    }
}
