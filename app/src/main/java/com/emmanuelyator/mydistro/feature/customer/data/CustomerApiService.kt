package com.emmanuelyator.mydistro.feature.customer.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class OrderItemRequest(val stockItemId: String, val quantity: Int)
data class OrderRequest(val organizationId: String, val deliveryAddress: String, val items: List<OrderItemRequest>)
data class OrderResponse(val id: String)

data class PaymentInitiateRequest(val orderId: String, val phoneNumber: String)
data class PaymentInitiateResponse(val checkoutRequestID: String, val customerMessage: String)

interface CustomerApiService {
    @POST("api/v1/orders")
    suspend fun createOrder(@Body request: OrderRequest): Response<OrderResponse>
    
    @POST("api/v1/payments/initiate")
    suspend fun initiatePayment(@Body request: PaymentInitiateRequest): Response<PaymentInitiateResponse>
}
