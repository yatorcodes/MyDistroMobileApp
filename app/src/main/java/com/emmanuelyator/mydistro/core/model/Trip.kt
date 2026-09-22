package com.emmanuelyator.mydistro.core.model

/**
 * A trip is one driver journey. It may carry several orders, so trip status and
 * stop status are deliberately tracked separately: a trip can be ONGOING while
 * individual stops are still PENDING.
 */
data class Trip(
    val id: String,
    val tripNumber: String,
    val type: TripType,
    val status: TripStatus,
    /** Where the driver loads: a distributor warehouse for DELIVERY, a factory for RESTOCK. */
    val origin: TripLocation,
    /** Ordered by [TripStop.sequence]. A RESTOCK trip normally has a single stop. */
    val stops: List<TripStop>,
    val itemsOnBoard: List<TripItem>,
    val scheduledStartLabel: String,
    val totalDistanceKm: Int
) {
    val completedStopCount: Int get() = stops.count { it.status == StopStatus.COMPLETED }

    val totalStopCount: Int get() = stops.size

    /** 0f..1f, safe when a trip has no stops yet. */
    val progress: Float
        get() = if (stops.isEmpty()) 0f else completedStopCount.toFloat() / stops.size

    /** The next stop the driver should act on, or null when the trip is finished. */
    val activeStop: TripStop?
        get() = stops.firstOrNull { it.status == StopStatus.IN_PROGRESS }
            ?: stops.firstOrNull { it.status == StopStatus.PENDING }
}

enum class TripType { DELIVERY, RESTOCK }

enum class TripStatus { SCHEDULED, ONGOING, COMPLETED, DELAYED, CANCELLED }

enum class StopStatus { PENDING, IN_PROGRESS, COMPLETED, FAILED, SKIPPED }

data class TripLocation(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)

/**
 * One halt on the route. [orderNumber] is null for stops that are not customer
 * deliveries, such as the distributor warehouse leg of a restock trip.
 */
data class TripStop(
    val id: String,
    val sequence: Int,
    val location: TripLocation,
    val status: StopStatus,
    val etaLabel: String,
    val distanceFromPreviousKm: Int,
    val orderNumber: String? = null,
    val contactName: String? = null,
    val contactPhone: String? = null
) {
    val requiresDeliveryConfirmation: Boolean get() = orderNumber != null
}

/** Freight on the vehicle, aggregated across every order in the trip. */
data class TripItem(
    val id: String,
    val productName: String,
    val brand: String,
    val unitLabel: String,
    val quantity: Int
)
