package com.emmanuelyator.mydistro.feature.customer.cart

import com.emmanuelyator.mydistro.core.common.AppError
import com.emmanuelyator.mydistro.core.common.DataResult
import com.emmanuelyator.mydistro.core.common.Validators
import com.emmanuelyator.mydistro.core.model.DeliveryAddress
import com.emmanuelyator.mydistro.feature.customer.data.OrderItemRequest
import com.emmanuelyator.mydistro.feature.customer.domain.OrderRepository
import com.emmanuelyator.mydistro.feature.customer.domain.PaymentProvider
import java.util.UUID

/** Outcome of placing an order and waiting for the provider to settle. */
sealed interface CheckoutResult {
    data class Paid(val orderId: String, val message: String) : CheckoutResult
    data class Failed(val message: String) : CheckoutResult
}

/**
 * Places one unpaid order for a single supplier, starts Daraja STK or Paystack
 * card checkout, and polls LogiFlow status until confirmed (or the window runs
 * out). Browser/SDK return is never treated as paid. The cart is cleared only
 * after [CheckoutResult.Paid].
 */
class CustomerCheckout(
    private val orderRepository: OrderRepository,
    private val pollIntervalMs: Long = 2_000L,
    private val pollTimeoutMs: Long = 90_000L,
    private val delay: suspend (Long) -> Unit = { kotlinx.coroutines.delay(it) },
    private val newIdempotencyKey: () -> String = { UUID.randomUUID().toString() }
) {

    /**
     * [idempotencyKey] is reused when the same payment attempt is retried.
     * Pass null to mint a new key for a fresh attempt (only after a definitive
     * failure). [onAuthorizationUrl] opens Paystack hosted checkout; it is not
     * confirmation of payment.
     */
    suspend fun checkout(
        items: List<CartItemUiModel>,
        address: DeliveryAddress?,
        provider: PaymentProvider,
        phoneNumber: String?,
        idempotencyKey: String? = null,
        onAuthorizationUrl: suspend (url: String, providerMode: String?) -> Unit = { _, _ -> }
    ): Pair<CheckoutResult, String> {
        val key = idempotencyKey ?: newIdempotencyKey()
        val validationError = validate(items, address, provider, phoneNumber)
        if (validationError != null) {
            return CheckoutResult.Failed(validationError) to key
        }
        val organizationId = items.map { it.product.organizationId.trim() }.distinct().single()
        val orderItems = items.map {
            OrderItemRequest(stockItemId = it.product.id, quantity = it.quantity)
        }
        val normalizedPhone = phoneNumber?.trim()?.takeIf { it.isNotEmpty() }?.let {
            Validators.toE164Kenya(it)
        }

        when (val placed = orderRepository.placeOrder(organizationId, address!!, orderItems)) {
            is DataResult.Failure -> return CheckoutResult.Failed(placed.error.message) to key
            is DataResult.Success -> {
                val orderId = placed.data.id
                when (
                    val initiated = orderRepository.initiatePayment(
                        orderId = orderId,
                        provider = provider,
                        phoneNumber = if (provider == PaymentProvider.DARAJA) normalizedPhone else null,
                        idempotencyKey = key
                    )
                ) {
                    is DataResult.Failure ->
                        return CheckoutResult.Failed(initiated.error.message) to key
                    is DataResult.Success -> {
                        val mode = initiated.data.providerMode
                        val authUrl = initiated.data.authorizationUrl?.takeIf { it.isNotBlank() }
                        if (provider == PaymentProvider.PAYSTACK) {
                            if (authUrl != null) {
                                onAuthorizationUrl(authUrl, mode)
                            }
                        }
                        val promptMessage = when (provider) {
                            PaymentProvider.DARAJA ->
                                initiated.data.customerMessage?.takeIf { it.isNotBlank() }
                                    ?: "Payment prompt sent. Approve the M-Pesa STK request on your phone."
                            PaymentProvider.PAYSTACK ->
                                initiated.data.customerMessage?.takeIf { it.isNotBlank() }
                                    ?: if (authUrl != null) {
                                        "Complete card payment in the browser. We confirm only from our servers."
                                    } else {
                                        "Payment started. Refresh status — do not pay again yet."
                                    }
                        }
                        val paid = awaitPayment(orderId, provider)
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

    private suspend fun awaitPayment(orderId: String, provider: PaymentProvider): DataResult<Unit> {
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
        val waitingHint = when (provider) {
            PaymentProvider.DARAJA ->
                "Still waiting for M-Pesa. Complete the prompt on your phone, then open the order to check status. Your cart was kept."
            PaymentProvider.PAYSTACK ->
                "Still confirming card payment. Return from the browser and open the order — do not pay again yet. Your cart was kept."
        }
        return DataResult.Failure(
            AppError.Validation(lastFailure?.takeIf { it.isNotBlank() } ?: waitingHint)
        )
    }

    companion object {
        fun validate(
            items: List<CartItemUiModel>,
            address: DeliveryAddress?,
            provider: PaymentProvider,
            phoneNumber: String?
        ): String? {
            if (items.isEmpty()) return "Cart is empty"
            if (address == null) return "Choose a delivery address before paying."
            if (address.summary.isBlank()) return "The selected delivery address is incomplete."
            if (provider == PaymentProvider.DARAJA) {
                val phone = phoneNumber?.trim().orEmpty()
                if (!Validators.isValidKenyanPhone(phone)) {
                    return "Enter a valid Kenyan M-Pesa phone number."
                }
            }
            val orgIds = items.map { it.product.organizationId.trim() }.filter { it.isNotEmpty() }.distinct()
            if (orgIds.isEmpty()) {
                return "These products are missing a supplier. Refresh the catalogue and try again."
            }
            if (orgIds.size > 1) {
                return "Checkout is for one supplier at a time. Remove items from the other suppliers, then pay."
            }
            return null
        }

        /** Back-compat for callers that still pass phone-only validation. */
        fun validate(
            items: List<CartItemUiModel>,
            address: DeliveryAddress?,
            phoneNumber: String
        ): String? = validate(items, address, PaymentProvider.DARAJA, phoneNumber)

        fun paymentStatusLabel(status: String?): String = when (status?.uppercase()) {
            "CONFIRMED", "RECONCILED" -> "Paid"
            "PENDING", "INITIATING" -> "Pending"
            "FAILED" -> "Failed"
            "REVERSED" -> "Reversed"
            null, "" -> "Unknown"
            else -> status
        }

        fun providerModeLabel(mode: String?): String? = when (mode?.uppercase()) {
            "TEST" -> "TEST (sandbox)"
            "LIVE" -> "LIVE"
            null, "" -> null
            else -> mode
        }
    }
}
