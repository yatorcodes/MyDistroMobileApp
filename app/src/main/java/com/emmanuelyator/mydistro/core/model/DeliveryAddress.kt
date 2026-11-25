package com.emmanuelyator.mydistro.core.model

/**
 * A place a customer can have an order delivered. A customer can save several
 * (a shop and a branch, say); one of them is the default and is pre-selected at
 * checkout.
 */
data class DeliveryAddress(
    val id: String?,
    val label: String,
    val recipientName: String,
    val phoneNumber: String,
    val addressLine: String,
    val city: String = "",
    val county: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isDefault: Boolean = false
) {
    /** The single line sent to the backend as the order's delivery address. */
    val summary: String
        get() = listOf(addressLine, city, county)
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .joinToString(", ")
}
