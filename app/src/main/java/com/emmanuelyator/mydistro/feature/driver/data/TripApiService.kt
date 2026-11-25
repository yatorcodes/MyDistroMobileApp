package com.emmanuelyator.mydistro.feature.driver.data

import com.emmanuelyator.mydistro.core.model.Trip
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class TripPageDto(
    val content: List<Trip> = emptyList()
)

interface TripApiService {
    @GET("api/v1/trips/active")
    suspend fun getActiveTrips(): Response<List<Trip>>

    @GET("api/v1/trips/history")
    suspend fun getTripHistory(): Response<List<Trip>>

    @GET("api/v1/trips")
    suspend fun listTrips(
        @retrofit2.http.Query("page") page: Int = 0,
        @retrofit2.http.Query("size") size: Int = 50
    ): Response<TripPageDto>

    @GET("api/v1/trips/{tripId}")
    suspend fun getTrip(@Path("tripId") tripId: String): Response<Trip>

    @GET("api/v1/trips/{tripId}/tracking")
    suspend fun getTripTracking(@Path("tripId") tripId: String): Response<List<LocationPingResponse>>

    @GET("api/v1/trips/{tripId}/location")
    suspend fun getTripLocationHistory(@Path("tripId") tripId: String): Response<List<LocationPingResponse>>

    @POST("api/v1/trips/{tripId}/stops/{stopId}/confirm")
    suspend fun confirmStopDelivery(
        @Path("tripId") tripId: String,
        @Path("stopId") stopId: String,
        @Body request: ConfirmStopRequest
    ): Response<Trip>

    @POST("api/v1/trips/{tripId}/stops/{stopId}/delivery-otp")
    suspend fun requestStopDeliveryOtp(
        @Path("tripId") tripId: String,
        @Path("stopId") stopId: String
    ): Response<Unit>

    @POST("api/v1/trips/{tripId}/location")
    suspend fun pingTripLocation(
        @Path("tripId") tripId: String,
        @Body request: LocationPingRequest
    ): Response<LocationPingResponse>

    @POST("api/v1/drivers/me/location")
    suspend fun pingDriverLocation(@Body request: LocationPingRequest): Response<Unit>
}

data class ConfirmStopRequest(val otp: String)

data class LocationPingRequest(val lat: Double, val lng: Double)

data class LocationPingResponse(
    val id: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val recordedAt: String? = null
)
