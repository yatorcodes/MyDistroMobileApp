package com.emmanuelyator.mydistro.feature.driver.data

import com.emmanuelyator.mydistro.core.model.Trip
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TripApiService {
    @GET("api/v1/trips/active")
    suspend fun getActiveTrips(): Response<List<Trip>>

    @GET("api/v1/trips/history")
    suspend fun getTripHistory(): Response<List<Trip>>

    @POST("api/v1/trips/{tripId}/stops/{stopId}/confirm")
    suspend fun confirmStopDelivery(
        @Path("tripId") tripId: String,
        @Path("stopId") stopId: String,
        @Body request: ConfirmStopRequest
    ): Response<Trip>
}

data class ConfirmStopRequest(val otp: String)
