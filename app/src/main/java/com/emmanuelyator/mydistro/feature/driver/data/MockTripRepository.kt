package com.emmanuelyator.mydistro.feature.driver.data

import com.emmanuelyator.mydistro.core.common.AppError
import com.emmanuelyator.mydistro.core.common.DataResult
import com.emmanuelyator.mydistro.core.database.TripDao
import com.emmanuelyator.mydistro.core.database.toEntity
import com.emmanuelyator.mydistro.core.model.StopStatus
import com.emmanuelyator.mydistro.core.model.Trip
import com.emmanuelyator.mydistro.core.model.TripStatus
import com.emmanuelyator.mydistro.feature.driver.domain.TripRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * PROTOTYPE ONLY — trips come from [MockTripData] and OTPs are checked against a
 * constant. There is no server, no customer notification and no audit trail.
 *
 * What is real: the in-memory store behaves like a single source of truth, so
 * confirming a delivery propagates to every observing screen exactly as it will
 * once Retrofit is wired in. It also writes through to Room, which exercises the
 * offline cache path.
 *
 * Replacing it: implement [TripRepository] over ApiService + [TripDao] and
 * change the `@Binds` in
 * [com.emmanuelyator.mydistro.feature.driver.di.DriverModule].
 */
@Singleton
class MockTripRepository @Inject constructor(
    private val tripDao: TripDao
) : TripRepository {

    private val trips = MutableStateFlow(MockTripData.allTrips)

    override fun observeActiveTrips(): Flow<List<Trip>> = trips.map { all ->
        all.filter { it.status.isActive }
            // Trips under way first, then scheduled ones.
            .sortedBy { trip -> if (trip.status == TripStatus.ONGOING) 0 else 1 }
    }

    override fun observeTripHistory(): Flow<List<Trip>> = trips.map { all ->
        all.filterNot { it.status.isActive }
    }

    override fun observeTrip(tripId: String): Flow<Trip?> = trips.map { all ->
        all.firstOrNull { it.id == tripId }
    }

    override suspend fun refresh(): DataResult<Unit> {
        delay(900)
        // Mirror into Room so the cache path is real even while the data is not.
        return runCatching {
            val now = System.currentTimeMillis()
            tripDao.replaceAll(trips.value.map { it.toEntity(now) })
        }.fold(
            onSuccess = { DataResult.Success(Unit) },
            onFailure = { DataResult.Failure(AppError.Unknown()) }
        )
    }

    override suspend fun confirmStopDelivery(
        tripId: String,
        stopId: String,
        otp: String
    ): DataResult<Trip> {
        delay(1_200)

        val trip = trips.value.firstOrNull { it.id == tripId }
            ?: return DataResult.Failure(AppError.Validation("That trip no longer exists."))

        val stop = trip.stops.firstOrNull { it.id == stopId }
            ?: return DataResult.Failure(AppError.Validation("That stop is not part of this trip."))

        if (stop.status == StopStatus.COMPLETED) {
            return DataResult.Failure(
                AppError.Validation("This delivery has already been confirmed.")
            )
        }

        // Mock check. Never log the submitted value — real OTPs must not reach
        // logcat, and treating the fake one carefully keeps that habit intact.
        if (otp != MockTripData.DEMO_OTP) {
            return DataResult.Failure(
                AppError.Validation("That OTP is not correct. Ask the customer to read it again.")
            )
        }

        val updatedStops = trip.stops.map { candidate ->
            when {
                candidate.id == stopId -> candidate.copy(status = StopStatus.COMPLETED)
                // Advance the next pending stop so the route stays coherent.
                candidate.status == StopStatus.PENDING &&
                    candidate.sequence == stop.sequence + 1 ->
                    candidate.copy(status = StopStatus.IN_PROGRESS)
                else -> candidate
            }
        }

        val allDone = updatedStops.all { it.status == StopStatus.COMPLETED }
        val updatedTrip = trip.copy(
            stops = updatedStops,
            status = if (allDone) TripStatus.COMPLETED else TripStatus.ONGOING
        )

        trips.value = trips.value.map { if (it.id == tripId) updatedTrip else it }
        return DataResult.Success(updatedTrip)
    }
}

private val TripStatus.isActive: Boolean
    get() = this == TripStatus.ONGOING || this == TripStatus.SCHEDULED || this == TripStatus.DELAYED
