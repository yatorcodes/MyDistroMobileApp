package com.emmanuelyator.mydistro.feature.customer.domain

/**
 * Customer-order payment rails. Distinct from distributor→manufacturer bank transfer.
 * Paystack here is hosted **card** checkout, not bank transfer.
 */
enum class PaymentProvider(val apiValue: String, val label: String) {
    DARAJA("DARAJA", "M-Pesa (Safaricom)"),
    PAYSTACK("PAYSTACK", "Card (Paystack)");
}
