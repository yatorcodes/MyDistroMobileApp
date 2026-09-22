package com.emmanuelyator.mydistro.feature.driver.data

import com.emmanuelyator.mydistro.core.common.DataResult
import com.emmanuelyator.mydistro.core.database.TripDao
import com.emmanuelyator.mydistro.core.database.TripEntity
import com.emmanuelyator.mydistro.core.model.StopStatus
import com.emmanuelyator.mydistro.core.model.TripStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Covers the delivery-confirmation rules that the UI depends on. These are the
 * behaviours a real backend implementation must also honour, so the tests stay
 * useful after the mock is replaced.
 */
class MockTripRepositoryTest {

    private fun repository() = MockTripRepository(FakeTripDao())

    @Test
    fun `active trips exclude completed ones and put the ongoing trip first`() = runBlocking {
        val active = repository().observeActiveTrips().first()

        assertTrue(active.none { it.status == TripStatus.COMPLETED })
        assertEquals(TripStatus.ONGOING, active.first().status)
    }

    @Test
    fun `history contains only finished trips`() = runBlocking {
        val history = repository().observeTripHistory().first()

        assertTrue(history.isNotEmpty())
        assertTrue(history.all { it.status == TripStatus.COMPLETED })
    }

    @Test
    fun `a wrong OTP is rejected and leaves the stop untouched`() = runBlocking {
        val repo = repository()
        val trip = MockTripData.deliveryTripInProgress
        val stop = trip.stops.first { it.status == StopStatus.IN_PROGRESS }

        val result = repo.confirmStopDelivery(trip.id, stop.id, otp = "000000")

        assertTrue(result is DataResult.Failure)
        val after = repo.observeTrip(trip.id).first()
        assertEquals(
            StopStatus.IN_PROGRESS,
            after?.stops?.first { it.id == stop.id }?.status
        )
    }

    @Test
    fun `the correct OTP completes the stop and promotes the next one`() = runBlocking {
        val repo = repository()
        val trip = MockTripData.deliveryTripInProgress
        val stop = trip.stops.first { it.status == StopStatus.IN_PROGRESS }
        val following = trip.stops.first { it.sequence == stop.sequence + 1 }

        val result = repo.confirmStopDelivery(trip.id, stop.id, MockTripData.DEMO_OTP)

        assertTrue(result is DataResult.Success)
        val updated = (result as DataResult.Success).data
        assertEquals(
            StopStatus.COMPLETED,
            updated.stops.first { it.id == stop.id }.status
        )
        // The route must move on, otherwise the driver has no next action.
        assertEquals(
            StopStatus.IN_PROGRESS,
            updated.stops.first { it.id == following.id }.status
        )
        assertEquals(2, updated.completedStopCount)
    }

    @Test
    fun `confirming the same stop twice is rejected`() = runBlocking {
        val repo = repository()
        val trip = MockTripData.deliveryTripInProgress
        val stop = trip.stops.first { it.status == StopStatus.IN_PROGRESS }

        repo.confirmStopDelivery(trip.id, stop.id, MockTripData.DEMO_OTP)
        val second = repo.confirmStopDelivery(trip.id, stop.id, MockTripData.DEMO_OTP)

        assertTrue(second is DataResult.Failure)
    }

    @Test
    fun `a trip becomes completed once every stop is confirmed`() = runBlocking {
        val repo = repository()
        val trip = MockTripData.deliveryTripInProgress

        // Stop 1 is already delivered in the fixture; confirm the remaining two.
        trip.stops
            .filter { it.status != StopStatus.COMPLETED }
            .sortedBy { it.sequence }
            .forEach { stop ->
                repo.confirmStopDelivery(trip.id, stop.id, MockTripData.DEMO_OTP)
            }

        val finished = repo.observeTrip(trip.id).first()
        assertNotNull(finished)
        assertEquals(TripStatus.COMPLETED, finished!!.status)
        assertEquals(1f, finished.progress, 0.001f)
    }

    @Test
    fun `restock trips expose a stop that needs no delivery confirmation`() {
        val stop = MockTripData.restockTripScheduled.stops.single()

        // The UI relies on this to hide the OTP call to action on restock trips.
        assertEquals(false, stop.requiresDeliveryConfirmation)
    }
}

/** In-memory stand-in so the repository can be tested without Robolectric. */
private class FakeTripDao : TripDao {
    private val rows = MutableStateFlow<List<TripEntity>>(emptyList())

    override fun observeTrips(): Flow<List<TripEntity>> = rows

    override suspend fun getTrip(tripId: String): TripEntity? =
        rows.value.firstOrNull { it.id == tripId }

    override suspend fun upsertAll(trips: List<TripEntity>) {
        rows.value = rows.value.filterNot { existing -> trips.any { it.id == existing.id } } + trips
    }

    override suspend fun clear() {
        rows.value = emptyList()
    }
}
