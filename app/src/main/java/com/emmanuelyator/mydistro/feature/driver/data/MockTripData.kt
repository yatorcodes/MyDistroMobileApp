package com.emmanuelyator.mydistro.feature.driver.data

import com.emmanuelyator.mydistro.core.model.StopStatus
import com.emmanuelyator.mydistro.core.model.Trip
import com.emmanuelyator.mydistro.core.model.TripItem
import com.emmanuelyator.mydistro.core.model.TripLocation
import com.emmanuelyator.mydistro.core.model.TripStatus
import com.emmanuelyator.mydistro.core.model.TripStop
import com.emmanuelyator.mydistro.core.model.TripType

/**
 * Sample trips along real Kenyan distribution corridors, used until the backend
 * is available. Coordinates are approximate town centres — good enough to place
 * a marker, not survey-grade.
 *
 * Kept in its own file so deleting the mock layer later is a clean removal.
 */
internal object MockTripData {

    private val nairobiDistributor = TripLocation(
        name = "Nairobi (Distributor)",
        address = "Baba Dogo Road, Ruaraka, Nairobi",
        latitude = -1.2394,
        longitude = 36.8785
    )

    private val athiRiverFactory = TripLocation(
        name = "Athi River (Factory)",
        address = "EPZ Road, Athi River, Machakos",
        latitude = -1.4565,
        longitude = 36.9784
    )

    /**
     * A multi-stop delivery run down the Mombasa Road / Kitui corridor, matching
     * the reference design: two stops done, one still to go.
     */
    val deliveryTripInProgress = Trip(
        id = "trip-1042",
        tripNumber = "TRIP-1042",
        type = TripType.DELIVERY,
        status = TripStatus.ONGOING,
        origin = nairobiDistributor,
        scheduledStartLabel = "Today, 8:00 AM",
        totalDistanceKm = 320,
        stops = listOf(
            TripStop(
                id = "stop-1042-1",
                sequence = 1,
                location = TripLocation(
                    name = "Kibwezi Hardware",
                    address = "Kibwezi Town, Makueni",
                    latitude = -2.4069,
                    longitude = 37.9689
                ),
                status = StopStatus.COMPLETED,
                etaLabel = "12:30 PM",
                distanceFromPreviousKm = 190,
                orderNumber = "ORD-4581",
                contactName = "Joseph Kimani",
                contactPhone = "+254720111222"
            ),
            TripStop(
                id = "stop-1042-2",
                sequence = 2,
                location = TripLocation(
                    name = "Kitui Building Supplies",
                    address = "Kalundu, Kitui Town",
                    latitude = -1.3667,
                    longitude = 38.0106
                ),
                status = StopStatus.IN_PROGRESS,
                etaLabel = "3:45 PM",
                distanceFromPreviousKm = 120,
                orderNumber = "ORD-4587",
                contactName = "Grace Mueni",
                contactPhone = "+254733444555"
            ),
            TripStop(
                id = "stop-1042-3",
                sequence = 3,
                location = TripLocation(
                    name = "Mwingi Hardware",
                    address = "Mwingi Town, Kitui",
                    latitude = -0.9333,
                    longitude = 38.0600
                ),
                status = StopStatus.PENDING,
                etaLabel = "5:30 PM",
                distanceFromPreviousKm = 95,
                orderNumber = "ORD-4592",
                contactName = "Peter Musyoka",
                contactPhone = "+254711888999"
            )
        ),
        itemsOnBoard = listOf(
            TripItem(
                id = "item-1042-1",
                productName = "Cement (Bamburi)",
                brand = "Bamburi",
                unitLabel = "50kg bag",
                quantity = 200
            ),
            TripItem(
                id = "item-1042-2",
                productName = "Cement (Simba)",
                brand = "Simba",
                unitLabel = "50kg bag",
                quantity = 150
            ),
            TripItem(
                id = "item-1042-3",
                productName = "Steel Rods D12",
                brand = "Devki",
                unitLabel = "12m length",
                quantity = 80
            )
        )
    )

    /**
     * A factory-to-warehouse restock. Single stop and no customer OTP, which is
     * exactly the case the UI must not assume away.
     */
    val restockTripScheduled = Trip(
        id = "trip-1037",
        tripNumber = "TRIP-1037",
        type = TripType.RESTOCK,
        status = TripStatus.SCHEDULED,
        origin = athiRiverFactory,
        scheduledStartLabel = "Today, 2:00 PM",
        totalDistanceKm = 80,
        stops = listOf(
            TripStop(
                id = "stop-1037-1",
                sequence = 1,
                location = nairobiDistributor,
                status = StopStatus.PENDING,
                etaLabel = "4:00 PM",
                distanceFromPreviousKm = 80,
                // No order number: warehouse intake is confirmed by the
                // distributor in the web console, not by a customer OTP.
                orderNumber = null,
                contactName = "Warehouse Desk",
                contactPhone = "+254700123456"
            )
        ),
        itemsOnBoard = listOf(
            TripItem(
                id = "item-1037-1",
                productName = "Cement (Bamburi)",
                brand = "Bamburi",
                unitLabel = "50kg bag",
                quantity = 600
            )
        )
    )

    private val completedDeliveryTrip = Trip(
        id = "trip-1021",
        tripNumber = "TRIP-1021",
        type = TripType.DELIVERY,
        status = TripStatus.COMPLETED,
        origin = nairobiDistributor,
        scheduledStartLabel = "Sep 18, 7:30 AM",
        totalDistanceKm = 145,
        stops = listOf(
            TripStop(
                id = "stop-1021-1",
                sequence = 1,
                location = TripLocation(
                    name = "Thika Hardware Centre",
                    address = "Kenyatta Highway, Thika",
                    latitude = -1.0333,
                    longitude = 37.0693
                ),
                status = StopStatus.COMPLETED,
                etaLabel = "9:10 AM",
                distanceFromPreviousKm = 45,
                orderNumber = "ORD-4410",
                contactName = "Daniel Njoroge",
                contactPhone = "+254722333444"
            ),
            TripStop(
                id = "stop-1021-2",
                sequence = 2,
                location = TripLocation(
                    name = "Muranga Builders",
                    address = "Muranga Town",
                    latitude = -0.7167,
                    longitude = 37.1500
                ),
                status = StopStatus.COMPLETED,
                etaLabel = "11:40 AM",
                distanceFromPreviousKm = 55,
                orderNumber = "ORD-4415",
                contactName = "Alice Wanjiru",
                contactPhone = "+254701222333"
            )
        ),
        itemsOnBoard = listOf(
            TripItem(
                id = "item-1021-1",
                productName = "Paint (Crown Silk)",
                brand = "Crown",
                unitLabel = "20L drum",
                quantity = 40
            )
        )
    )

    private val completedRestockTrip = Trip(
        id = "trip-1014",
        tripNumber = "TRIP-1014",
        type = TripType.RESTOCK,
        status = TripStatus.COMPLETED,
        origin = athiRiverFactory,
        scheduledStartLabel = "Sep 15, 6:45 AM",
        totalDistanceKm = 80,
        stops = listOf(
            TripStop(
                id = "stop-1014-1",
                sequence = 1,
                location = nairobiDistributor,
                status = StopStatus.COMPLETED,
                etaLabel = "8:50 AM",
                distanceFromPreviousKm = 80,
                orderNumber = null,
                contactName = "Warehouse Desk",
                contactPhone = "+254700123456"
            )
        ),
        itemsOnBoard = listOf(
            TripItem(
                id = "item-1014-1",
                productName = "Steel Rods D10",
                brand = "Devki",
                unitLabel = "12m length",
                quantity = 320
            )
        )
    )

    /** Everything the fake "server" knows about, active and historical. */
    val allTrips: List<Trip> = listOf(
        deliveryTripInProgress,
        restockTripScheduled,
        completedDeliveryTrip,
        completedRestockTrip
    )

    /**
     * Stand-in for the OTP the customer receives by SMS. Real OTPs are generated
     * and verified server-side and must never be shipped in the client.
     */
    const val DEMO_OTP = "184279"
}
