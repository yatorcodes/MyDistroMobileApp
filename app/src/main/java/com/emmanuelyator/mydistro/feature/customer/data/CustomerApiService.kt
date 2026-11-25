package com.emmanuelyator.mydistro.feature.customer.data

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

data class OrderItemRequest(val stockItemId: String, val quantity: Int)

data class OrderRequest(
    val organizationId: String,
    val deliveryAddress: String,
    val items: List<OrderItemRequest>,
    val deliveryLat: Double? = null,
    val deliveryLng: Double? = null
)

data class CustomerAddressDto(
    val id: String? = null,
    val label: String? = null,
    val recipientName: String? = null,
    val phoneNumber: String? = null,
    val addressLine: String? = null,
    val city: String? = null,
    val county: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val defaultAddress: Boolean = false
)

data class CustomerAddressRequest(
    val label: String,
    val recipientName: String,
    val phoneNumber: String,
    val addressLine: String,
    val city: String? = null,
    val county: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val defaultAddress: Boolean = false
)

data class OrderResponseDto(
    val id: String,
    val orderNumber: String? = null,
    val status: String? = null,
    val totalAmount: Double? = null,
    val placedAt: String? = null,
    val items: List<OrderItemResponseDto>? = null,
    val deliveryAddress: String? = null,
    val organizationId: String? = null
)

data class OrderItemResponseDto(
    val id: String? = null,
    val stockItemId: String? = null,
    val quantity: Int? = null,
    val unitPrice: Double? = null,
    val lineTotal: Double? = null
)

data class OrderPageDto(
    val content: List<OrderResponseDto> = emptyList()
)

data class PaymentInitiateRequest(
    val orderId: String,
    val phoneNumber: String,
    val idempotencyKey: String? = null
)

data class PaymentInitiateResponse(
    val paymentId: String? = null,
    val mpesaCheckoutRequestId: String? = null,
    val status: String? = null,
    val customerMessage: String? = null
)

data class PaymentStatusResponse(
    val orderId: String? = null,
    val paymentId: String? = null,
    val status: String? = null,
    val amount: Double? = null,
    val mpesaReceiptNumber: String? = null,
    val failureReason: String? = null
)

data class OrderStatusUpdateRequest(
    val status: String,
    val note: String? = null
)

data class PaymentResponseDto(
    val id: String? = null,
    val orderId: String? = null,
    val amount: Double? = null,
    val method: String? = null,
    val status: String? = null,
    val mpesaReceiptNumber: String? = null,
    val initiatedAt: String? = null,
    val confirmedAt: String? = null
)

data class ProductDto(
    val id: String,
    val organizationId: String? = null,
    val sku: String? = null,
    val name: String,
    val category: String? = null,
    val brand: String? = null,
    val unit: String? = null,
    val description: String? = null,
    val unitPrice: Double? = null,
    val active: Boolean? = null,
    val imageUrl: String? = null,
    val inStock: Boolean? = null
)

data class ProductPageDto(
    val content: List<ProductDto> = emptyList()
)

interface CustomerApiService {
    @GET("api/v1/products")
    suspend fun getProducts(
        @Query("category") category: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 100
    ): Response<ProductPageDto>

    @GET("api/v1/products/categories")
    suspend fun getCategories(): Response<List<String>>

    @POST("api/v1/orders")
    suspend fun createOrder(@Body request: OrderRequest): Response<OrderResponseDto>

    @GET("api/v1/orders")
    suspend fun getOrders(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50
    ): Response<OrderPageDto>

    @GET("api/v1/orders/{orderId}")
    suspend fun getOrder(@Path("orderId") orderId: String): Response<OrderResponseDto>

    @POST("api/v1/payments/initiate")
    suspend fun initiatePayment(@Body request: PaymentInitiateRequest): Response<PaymentInitiateResponse>

    @GET("api/v1/payments/{orderId}/status")
    suspend fun getPaymentStatus(@Path("orderId") orderId: String): Response<PaymentStatusResponse>

    @POST("api/v1/orders/{orderId}/delivery-otp")
    suspend fun requestDeliveryOtp(@Path("orderId") orderId: String): Response<Unit>

    @PATCH("api/v1/orders/{orderId}/status")
    suspend fun updateOrderStatus(
        @Path("orderId") orderId: String,
        @Body request: OrderStatusUpdateRequest
    ): Response<OrderResponseDto>

    @GET("api/v1/orders/{orderId}/payments")
    suspend fun getOrderPayments(@Path("orderId") orderId: String): Response<PaymentResponseDto>

    @GET("api/users/me/addresses")
    suspend fun getAddresses(): Response<List<CustomerAddressDto>>

    @POST("api/users/me/addresses")
    suspend fun createAddress(@Body request: CustomerAddressRequest): Response<CustomerAddressDto>

    @Streaming
    @GET("api/v1/products/{productId}/image")
    suspend fun getProductImage(@Path("productId") productId: String): Response<ResponseBody>
}
