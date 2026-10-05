package com.emmanuelyator.mydistro.feature.driver.data

import com.emmanuelyator.mydistro.core.common.AppError
import com.emmanuelyator.mydistro.core.common.DataResult
import com.emmanuelyator.mydistro.core.database.TripDao
import com.emmanuelyator.mydistro.core.database.toEntity
import com.emmanuelyator.mydistro.core.model.Trip
import com.emmanuelyator.mydistro.core.model.TripStatus
import com.emmanuelyator.mydistro.feature.driver.domain.TripRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

@Singleton
class NetworkTripRepository @Inject constructor(
    private val apiService: TripApiService,
    private val tripDao: TripDao
) : TripRepository {
    
    // Same memory cache pattern as the mock, but populated by the server
    private val trips = MutableStateFlow<List<Trip>>(emptyList())
    
    override fun observeActiveTrips(): Flow<List<Trip>> = trips.map { all ->
        all.filter { it.status.isActive }
            .sortedBy { trip -> if (trip.status == TripStatus.ONGOING) 0 else 1 }
    }
    
    override fun observeTripHistory(): Flow<List<Trip>> = trips.map { all ->
        all.filterNot { it.status.isActive }
    }
    
    override fun observeTrip(tripId: String): Flow<Trip?> = trips.map { all ->
        all.firstOrNull { it.id == tripId }
    }
    
    override suspend fun refresh(): DataResult<Unit> {
        return try {
            // Fetch both Active and History from your Spring Boot backend
            val activeResponse = apiService.getActiveTrips()
            val historyResponse = apiService.getTripHistory()
            if (activeResponse.isSuccessful && historyResponse.isSuccessful) {
                val activeTrips = activeResponse.body() ?: emptyList()
                val historyTrips = historyResponse.body() ?: emptyList()
                
                // Merge them together
                val allTrips = activeTrips + historyTrips
                // Update UI flow
                trips.value = allTrips
                
                // Save everything to Room DB
                val now = System.currentTimeMillis()
                tripDao.replaceAll(allTrips.map { it.toEntity(now) })
                
                DataResult.Success(Unit)
            } else {
                DataResult.Failure(AppError.Network("Server error syncing trips"))
            }
        } catch (e: Exception) {
            DataResult.Failure(AppError.Network(e.message ?: "Connection failed"))
        }
    }
    
    override suspend fun confirmStopDelivery(
        tripId: String,
        stopId: String,
        otp: String
    ): DataResult<Trip> {
        return try {
            val response = apiService.confirmStopDelivery(tripId, stopId, ConfirmStopRequest(otp))
            
            if (response.isSuccessful && response.body() != null) {
                val updatedTrip = response.body()!!
                // Replace the old trip in our state flow with the updated one from the server
                trips.value = trips.value.map { if (it.id == tripId) updatedTrip else it }
                DataResult.Success(updatedTrip)
            } else if (response.code() == 400) {
                DataResult.Failure(AppError.Validation("That OTP is not correct. Ask the customer to read it again."))
            } else {
                DataResult.Failure(AppError.Network("Server returned ${response.code()}"))
            }
        } catch (e: Exception) {
            DataResult.Failure(AppError.Network(e.message ?: "Connection failed"))
        }
    }
}

private val TripStatus.isActive: Boolean
    get() = this == TripStatus.ONGOING || this == TripStatus.SCHEDULED || this == TripStatus.DELAYED
