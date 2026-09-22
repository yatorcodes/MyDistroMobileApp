package com.emmanuelyator.mydistro.core.network

import retrofit2.http.*

/**
 * Placeholder endpoints matching the backend design discussed earlier.
 * Fill in real request/response DTOs once the Spring Boot contract is confirmed —
 * these signatures will very likely need to change to match the actual API.
 */
interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("trips")
    suspend fun getMyTrips(): List<TripDto>

    @POST("trips/{tripId}/confirm-delivery")
    suspend fun confirmDelivery(
        @Path("tripId") tripId: String,
        @Body request: ConfirmDeliveryRequest
    ): TripDto

    @GET("products")
    suspend fun getProducts(): List<ProductDto>

    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): OrderDto

    @GET("orders")
    suspend fun getMyOrders(): List<OrderDto>
}

// --- Placeholder DTOs — match these to your actual backend response shapes ---

data class LoginRequest(val phoneOrEmail: String, val password: String)
data class LoginResponse(val token: String, val role: String, val userId: String)

data class TripDto(val id: String, val type: String, val status: String)
data class ConfirmDeliveryRequest(val otp: String, val orderNumber: String)

data class ProductDto(val id: String, val name: String, val brand: String, val unitPrice: String)

data class CreateOrderRequest(val items: List<OrderItemRequest>)
data class OrderItemRequest(val productId: String, val quantity: Int)
data class OrderDto(val id: String, val status: String, val totalAmount: String)