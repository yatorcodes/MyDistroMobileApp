package com.emmanuelyator.mydistro.feature.driver.deliveryconfirmation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmanuelyator.mydistro.core.common.DataResult
import com.emmanuelyator.mydistro.core.model.Trip
import com.emmanuelyator.mydistro.core.model.TripStop
import com.emmanuelyator.mydistro.feature.driver.domain.TripRepository
import com.emmanuelyator.mydistro.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

const val OTP_LENGTH = 6

data class DeliveryConfirmationUiState(
    val trip: Trip? = null,
    val stop: TripStop? = null,
    val otp: String = "",
    val otpError: String? = null,
    val isSubmitting: Boolean = false,
    val isConfirmed: Boolean = false,
    val loadError: String? = null
) {
    val isLoading: Boolean get() = trip == null && loadError == null

    /**
     * Blocks submission while a request is in flight and after success, which is
     * the guard against a double tap confirming the same delivery twice.
     */
    val canSubmit: Boolean
        get() = otp.length == OTP_LENGTH && !isSubmitting && !isConfirmed
}

@HiltViewModel
class DeliveryConfirmationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tripRepository: TripRepository
) : ViewModel() {

    private val tripId: String = checkNotNull(savedStateHandle[Routes.Args.TRIP_ID])
    private val stopId: String = checkNotNull(savedStateHandle[Routes.Args.STOP_ID])

    private val _uiState = MutableStateFlow(DeliveryConfirmationUiState())
    val uiState: StateFlow<DeliveryConfirmationUiState> = _uiState.asStateFlow()

    init {
        loadStop()
    }

    fun onOtpChange(value: String) {
        _uiState.value = _uiState.value.copy(otp = value, otpError = null)
    }

    fun onConfirm() {
        val current = _uiState.value
        if (!current.canSubmit) {
            if (current.otp.length != OTP_LENGTH && !current.isSubmitting) {
                _uiState.value = current.copy(
                    otpError = "Enter all $OTP_LENGTH digits of the customer's code."
                )
            }
            return
        }

        _uiState.value = current.copy(isSubmitting = true, otpError = null)

        viewModelScope.launch {
            val result = tripRepository.confirmStopDelivery(
                tripId = tripId,
                stopId = stopId,
                otp = current.otp
            )
            _uiState.value = when (result) {
                is DataResult.Success -> _uiState.value.copy(
                    isSubmitting = false,
                    isConfirmed = true,
                    trip = result.data,
                    stop = result.data.stops.firstOrNull { it.id == stopId }
                )

                is DataResult.Failure -> _uiState.value.copy(
                    isSubmitting = false,
                    otpError = result.error.message
                )
            }
        }
    }

    private fun loadStop() {
        viewModelScope.launch {
            val trip = tripRepository.observeTrip(tripId).first()
            if (trip == null) {
                _uiState.value = _uiState.value.copy(
                    loadError = "This trip is no longer assigned to you."
                )
                return@launch
            }
            val stop = trip.stops.firstOrNull { it.id == stopId }
            if (stop == null) {
                _uiState.value = _uiState.value.copy(
                    loadError = "This stop is not part of the trip."
                )
                return@launch
            }
            _uiState.value = _uiState.value.copy(trip = trip, stop = stop)
        }
    }
}
