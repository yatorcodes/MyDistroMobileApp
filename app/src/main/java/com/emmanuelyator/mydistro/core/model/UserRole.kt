package com.emmanuelyator.mydistro.core.model

/**
 * Roles the platform recognises. Only DRIVER and CUSTOMER sign in on mobile;
 * the other two are listed because the role picker explains where they belong.
 */
enum class UserRole {
    DRIVER,
    CUSTOMER,
    DISTRIBUTOR,
    FACTORY;

    /** Distributors and factories use the Angular web console, not this app. */
    val isSupportedOnMobile: Boolean get() = this == DRIVER || this == CUSTOMER
}

data class Driver(
    val id: String,
    val fullName: String,
    val phoneNumber: String,
    val vehicleRegistration: String,
    val avatarUrl: String? = null
) {
    /** "Alex Mwangi" -> "Alex", for the dashboard greeting. */
    val firstName: String get() = fullName.substringBefore(' ')
}
