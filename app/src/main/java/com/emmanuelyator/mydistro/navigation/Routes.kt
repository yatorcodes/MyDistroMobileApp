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
    const val DRIVER_MAP = "driver/map"
    const val DRIVER_HISTORY = "driver/history"
    const val DRIVER_PROFILE = "driver/profile"

    private const val DRIVER_TRIP_DETAILS_BASE = "driver/trip"
    const val DRIVER_TRIP_DETAILS = "$DRIVER_TRIP_DETAILS_BASE/{${Args.TRIP_ID}}"
    const val DRIVER_DELIVERY_CONFIRMATION =
        "$DRIVER_TRIP_DETAILS_BASE/{${Args.TRIP_ID}}/stop/{${Args.STOP_ID}}/confirm"

    fun driverTripDetails(tripId: String) = "$DRIVER_TRIP_DETAILS_BASE/$tripId"

    fun driverDeliveryConfirmation(tripId: String, stopId: String) =
        "$DRIVER_TRIP_DETAILS_BASE/$tripId/stop/$stopId/confirm"

    // --- Customer (later phases) ---
    const val CUSTOMER_COMING_SOON = "customer/coming_soon"

    object Args {
        const val TRIP_ID = "tripId"
        const val STOP_ID = "stopId"
    }
}

/** Destinations that make up the driver's bottom-navigation shell. */
val DriverShellRoutes = setOf(
    Routes.DRIVER_TRIPS,
    Routes.DRIVER_MAP,
    Routes.DRIVER_HISTORY,
    Routes.DRIVER_PROFILE
)
