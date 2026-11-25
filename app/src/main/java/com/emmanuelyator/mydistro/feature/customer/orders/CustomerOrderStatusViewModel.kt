package com.emmanuelyator.mydistro.feature.customer.orders

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmanuelyator.mydistro.core.common.DataResult
import com.emmanuelyator.mydistro.core.model.Order
import com.emmanuelyator.mydistro.core.model.OrderStatus
import com.emmanuelyator.mydistro.feature.customer.data.PaymentResponseDto
import com.emmanuelyator.mydistro.feature.customer.data.PaymentStatusResponse
import com.emmanuelyator.mydistro.feature.customer.domain.OrderRepository
import com.emmanuelyator.mydistro.feature.driver.data.LocationPingResponse
import com.emmanuelyator.mydistro.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CustomerOrderStatusUiState(
    val order: Order? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val paymentStatus: PaymentStatusResponse? = null,
    val paymentDetail: PaymentResponseDto? = null,
    val otpMessage: String? = null,
    val isRequestingOtp: Boolean = false,
    val isCancelling: Boolean = false,
    val tripId: String? = null,
    val stopId: String? = null,
    val tracking: List<LocationPingResponse> = emptyList(),
    val trackingLabel: String? = null
)

@HiltViewModel
class CustomerOrderStatusViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val orderId: String = checkNotNull(savedStateHandle[Routes.Args.ORDER_ID])

    private val _uiState = MutableStateFlow(CustomerOrderStatusUiState())
    val uiState: StateFlow<CustomerOrderStatusUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = orderRepository.getOrder(orderId)) {
                is DataResult.Success -> {
                    _uiState.update { it.copy(order = result.data, isLoading = false) }
                    loadPayments()
                    loadTracking(result.data)
                }
                is DataResult.Failure -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = result.error.message)
                    }
                }
            }
        }
    }

    fun requestDeliveryOtp() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRequestingOtp = true, otpMessage = null) }
            val tripId = _uiState.value.tripId
            val stopId = _uiState.value.stopId
            val result = if (tripId != null && stopId != null) {
                orderRepository.requestStopDeliveryOtp(tripId, stopId)
            } else {
                orderRepository.requestDeliveryOtp(orderId)
            }
            when (result) {
                is DataResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isRequestingOtp = false,
                            otpMessage = "Delivery OTP sent. Share it with the driver."
                        )
                    }
                }
                is DataResult.Failure -> {
                    _uiState.update {
                        it.copy(isRequestingOtp = false, otpMessage = result.error.message)
                    }
                }
            }
        }
    }

    fun cancelOrder() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCancelling = true, error = null) }
            when (val result = orderRepository.cancelOrder(orderId, note = "Cancelled by customer")) {
                is DataResult.Success -> {
                    _uiState.update { it.copy(order = result.data, isCancelling = false) }
                }
                is DataResult.Failure -> {
                    _uiState.update {
                        it.copy(isCancelling = false, error = result.error.message)
                    }
                }
            }
        }
    }

    private suspend fun loadPayments() {
        when (val result = orderRepository.getPaymentStatus(orderId)) {
            is DataResult.Success -> _uiState.update { it.copy(paymentStatus = result.data) }
            is DataResult.Failure -> Unit
        }
        when (val result = orderRepository.getOrderPayments(orderId)) {
            is DataResult.Success -> _uiState.update { it.copy(paymentDetail = result.data) }
            is DataResult.Failure -> Unit
        }
    }

    private suspend fun loadTracking(order: Order) {
        val pair = orderRepository.findTripStopForOrder(order)
        if (pair == null) {
            _uiState.update { it.copy(tripId = null, stopId = null, tracking = emptyList()) }
            return
        }
        val (tripId, stopId) = pair
        val pings = orderRepository.getTripTracking(tripId)
        val latest = pings.lastOrNull()
        _uiState.update {
            it.copy(
                tripId = tripId,
                stopId = stopId,
                tracking = pings,
                trackingLabel = latest?.let { ping ->
                    "Driver last seen at ${ping.lat}, ${ping.lng}"
                }
            )
        }
    }
}

fun CustomerOrderStatusUiState.canRequestDeliveryOtp(): Boolean =
    order?.status == OrderStatus.IN_TRANSIT

fun CustomerOrderStatusUiState.canCancel(): Boolean =
    order?.status == OrderStatus.PAYMENT_PENDING || order?.status == OrderStatus.SUBMITTED
