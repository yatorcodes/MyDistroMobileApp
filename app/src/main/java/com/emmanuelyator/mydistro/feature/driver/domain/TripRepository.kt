package com.emmanuelyator.mydistro.feature.driver.domain

import com.emmanuelyator.mydistro.core.common.DataResult
import com.emmanuelyator.mydistro.core.model.Trip
import kotlinx.coroutines.flow.Flow

/**
 * Read and update the signed-in driver's trips.
 *
 * Exposed as a [Flow] so the trip list, trip details and delivery confirmation
 * screens all observe one source of truth: confirming a delivery updates the
 * stop, and the list's progress bar moves without any screen telling another to
 * refresh.
 */
interface TripRepository {

    /** Active trips: anything the driver still has to act on. */
    fun observeActiveTrips(): Flow<List<Trip>>

    /** Finished or cancelled trips, most recent first. */
    fun observeTripHistory(): Flow<List<Trip>>

    fun observeTrip(tripId: String): Flow<Trip?>

    /** Fetches from the network and updates the cache. */
    suspend fun refresh(): DataResult<Unit>

    /**
     * Validates the customer's OTP for a stop and marks it delivered.
     *
     * The OTP check belongs to the backend — an implementation here is only ever
     * a stand-in. Returns the updated trip so callers can show new progress.
     */
    suspend fun confirmStopDelivery(
        tripId: String,
        stopId: String,
        otp: String
    ): DataResult<Trip>
}
