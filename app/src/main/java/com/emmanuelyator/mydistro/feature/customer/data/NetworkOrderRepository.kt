package com.emmanuelyator.mydistro.feature.customer.data

import com.emmanuelyator.mydistro.core.common.AppError
import com.emmanuelyator.mydistro.core.common.DataResult
import com.emmanuelyator.mydistro.core.model.DeliveryAddress
import com.emmanuelyator.mydistro.core.model.Order
import com.emmanuelyator.mydistro.feature.customer.domain.OrderRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class NetworkOrderRepository @Inject constructor(
    private val api: CustomerApiService,
    private val tripApi: com.emmanuelyator.mydistro.feature.driver.data.TripApiService
) : OrderRepository {

    private val orders = MutableStateFlow<List<Order>>(emptyList())

    override fun observeOrders(): Flow<List<Order>> = orders.asStateFlow()

    override suspend fun refreshOrders(): DataResult<Unit> {
        return try {
            val response = api.getOrders()
            if (response.isSuccessful) {
                orders.value = response.body()?.content.orEmpty().map { it.toOrder() }
                DataResult.Success(Unit)
            } else {
                DataResult.Failure(AppError.Network("Orders failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            DataResult.Failure(AppError.Network(e.message ?: "Connection failed"))
        }
    }

    override suspend fun placeOrder(
        organizationId: String,
        address: DeliveryAddress,
        items: List<OrderItemRequest>
    ): DataResult<Order> {
        return try {
            val response = api.createOrder(
                OrderRequest(
                    organizationId = organizationId,
                    deliveryAddress = address.summary,
                    items = items,
                    deliveryLat = address.latitude,
                    deliveryLng = address.longitude
                )
            )
            val body = response.body()
            if (response.isSuccessful && body != null) {
                val order = body.toOrder()
                orders.value = orders.value
                    .filterNot { it.id == order.id }
                    .plus(order)
                    .sortedByDescending { it.date }
                DataResult.Success(order)
            } else {
                response.toFailure("The order could not be placed.")
            }
        } catch (e: Exception) {
            e.toConnectionFailure()
        }
    }

    override suspend fun initiatePayment(
        orderId: String,
        provider: com.emmanuelyator.mydistro.feature.customer.domain.PaymentProvider,
        phoneNumber: String?,
        idempotencyKey: String
    ): DataResult<PaymentInitiateResponse> {
        return try {
            val response = api.initiatePayment(
                idempotencyKey = idempotencyKey,
                request = PaymentInitiateRequest(
                    orderId = orderId,
                    phoneNumber = phoneNumber?.takeIf { it.isNotBlank() },
                    idempotencyKey = idempotencyKey,
                    provider = provider.apiValue,
                    orderType = "CUSTOMER_ORDER"
                )
            )
            val body = response.body()
            if (response.isSuccessful && body != null) {
                DataResult.Success(body)
            } else {
                response.toFailure("The payment request could not be sent.")
            }
        } catch (e: Exception) {
            e.toConnectionFailure()
        }
    }

    override suspend fun getOrder(orderId: String): DataResult<Order> {
        return try {
            val response = api.getOrder(orderId)
            if (response.isSuccessful && response.body() != null) {
                val order = response.body()!!.toOrder()
                orders.value = orders.value
                    .filterNot { it.id == order.id }
                    .plus(order)
                    .sortedByDescending { it.date }
                DataResult.Success(order)
            } else {
                DataResult.Failure(AppError.Network("Order detail failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            DataResult.Failure(AppError.Network(e.message ?: "Connection failed"))
        }
    }

    override suspend fun getPaymentStatus(orderId: String): DataResult<PaymentStatusResponse> {
        return try {
            val response = api.getPaymentStatus(orderId)
            if (response.isSuccessful && response.body() != null) {
                DataResult.Success(response.body()!!)
            } else {
                DataResult.Failure(AppError.Network("Payment status failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            DataResult.Failure(AppError.Network(e.message ?: "Connection failed"))
        }
    }

    override suspend fun getOrderPayments(orderId: String): DataResult<PaymentResponseDto> {
        return try {
            val response = api.getOrderPayments(orderId)
            if (response.isSuccessful && response.body() != null) {
                DataResult.Success(response.body()!!)
            } else {
                DataResult.Failure(AppError.Network("Order payments failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            DataResult.Failure(AppError.Network(e.message ?: "Connection failed"))
        }
    }

    override suspend fun cancelOrder(orderId: String, note: String?): DataResult<Order> {
        return try {
            val response = api.updateOrderStatus(
                orderId,
                OrderStatusUpdateRequest(status = "CANCELLED", note = note)
            )
            if (response.isSuccessful && response.body() != null) {
                val order = response.body()!!.toOrder()
                orders.value = orders.value.map { if (it.id == order.id) order else it }
                DataResult.Success(order)
            } else {
                DataResult.Failure(AppError.Network("Cancel failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            DataResult.Failure(AppError.Network(e.message ?: "Connection failed"))
        }
    }

    override suspend fun requestDeliveryOtp(orderId: String): DataResult<Unit> {
        return try {
            val response = api.requestDeliveryOtp(orderId)
            if (response.isSuccessful) {
                DataResult.Success(Unit)
            } else {
                DataResult.Failure(AppError.Network("Delivery OTP failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            DataResult.Failure(AppError.Network(e.message ?: "Connection failed"))
        }
    }

    override suspend fun requestStopDeliveryOtp(tripId: String, stopId: String): DataResult<Unit> {
        return try {
            val response = tripApi.requestStopDeliveryOtp(tripId, stopId)
            if (response.isSuccessful) {
                DataResult.Success(Unit)
            } else {
                DataResult.Failure(AppError.Network("Stop OTP failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            DataResult.Failure(AppError.Network(e.message ?: "Connection failed"))
        }
    }

    override suspend fun findTripStopForOrder(order: Order): Pair<String, String>? {
        return try {
            val response = tripApi.listTrips(size = 50)
            if (!response.isSuccessful) return null
            val trips = response.body()?.content.orEmpty()
            val match = trips.firstOrNull { trip ->
                trip.stops.any { stop ->
                    stop.orderNumber == order.orderNumber || stop.orderNumber == order.id
                }
            } ?: return null
            val stop = match.stops.firstOrNull { stop ->
                stop.orderNumber == order.orderNumber || stop.orderNumber == order.id
            } ?: return null
            match.id to stop.id
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun getTripTracking(tripId: String): List<com.emmanuelyator.mydistro.feature.driver.data.LocationPingResponse> = try {
        val response = tripApi.getTripTracking(tripId)
        if (response.isSuccessful) response.body().orEmpty() else emptyList()
    } catch (_: Exception) {
        emptyList()
    }
}
