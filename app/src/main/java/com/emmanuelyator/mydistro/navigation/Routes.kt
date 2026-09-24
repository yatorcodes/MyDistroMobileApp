package com.emmanuelyator.mydistro.navigation

/**
 * Every destination in the app, in one place.
 *
 * String routes rather than Navigation 2.8's type-safe routes: the latter needs
 * kotlinx-serialization, and adding a serialisation plugin to a Kotlin 1.9.0
 * project is a bigger change than this slice justifies. Argument names are
 * centralised in [Args] so there are no magic strings at the call sites.
 */
object Routes {

    const val SPLASH = "splash"
    const val ROLE_SELECTION = "role_selection"

    // --- Driver ---
    const val DRIVER_LOGIN = "driver/login"
    const val DRIVER_PASSWORD_SETUP = "driver/password_setup"
    const val DRIVER_TRIPS = "driver/trips"
    const val DRIVER_MAP_BASE = "driver/map"
    const val DRIVER_MAP = "$DRIVER_MAP_BASE?${Args.TRIP_ID}={${Args.TRIP_ID}}"
    fun driverMap(tripId: String? = null): String {
        return if (tripId != null) "$DRIVER_MAP_BASE?${Args.TRIP_ID}=$tripId" else DRIVER_MAP_BASE
    }
    const val DRIVER_HISTORY = "driver/history"
    const val DRIVER_PROFILE = "driver/profile"
    const val DRIVER_EDIT_PROFILE = "driver/edit_profile"
    const val DRIVER_NOTIFICATIONS = "driver/notifications"
    const val DRIVER_NOTIFICATION_DETAILS = "driver/notifications/{notificationId}"
    fun driverNotificationDetails(id: String) = "driver/notifications/$id"
    const val DRIVER_PRIVACY_SECURITY = "driver/privacy_security"
    const val DRIVER_HELP_SUPPORT = "driver/help_support"

    private const val DRIVER_TRIP_DETAILS_BASE = "driver/trip"
    const val DRIVER_TRIP_DETAILS = "$DRIVER_TRIP_DETAILS_BASE/{${Args.TRIP_ID}}"
    const val DRIVER_DELIVERY_CONFIRMATION =
        "$DRIVER_TRIP_DETAILS_BASE/{${Args.TRIP_ID}}/stop/{${Args.STOP_ID}}/confirm"

    fun driverTripDetails(tripId: String) = "$DRIVER_TRIP_DETAILS_BASE/$tripId"

    fun driverDeliveryConfirmation(tripId: String, stopId: String) =
        "$DRIVER_TRIP_DETAILS_BASE/$tripId/stop/$stopId/confirm"

    // --- Customer ---
    const val CUSTOMER_LOGIN = "customer/login"
    const val CUSTOMER_SIGNUP = "customer/signup"
    const val CUSTOMER_HOME = "customer/home"
    const val CUSTOMER_ORDERS = "customer/orders"
    const val CUSTOMER_CART = "customer/cart"
    const val CUSTOMER_PROFILE = "customer/profile"
    const val CUSTOMER_COMING_SOON = "customer/coming_soon"

    object Args {
        const val TRIP_ID = "tripId"
        const val STOP_ID = "stopId"
    }
}

/** Destinations that make up the driver's bottom-navigation shell. */
val DriverShellRoutes = setOf(
    Routes.DRIVER_TRIPS,
    Routes.DRIVER_MAP_BASE,
    Routes.DRIVER_PROFILE
)

/** Destinations that make up the customer's bottom-navigation shell. */
val CustomerShellRoutes = setOf(
    Routes.CUSTOMER_HOME,
    Routes.CUSTOMER_ORDERS,
    Routes.CUSTOMER_CART,
    Routes.CUSTOMER_PROFILE
)
