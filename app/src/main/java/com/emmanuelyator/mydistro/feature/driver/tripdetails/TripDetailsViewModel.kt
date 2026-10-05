package com.emmanuelyator.mydistro.feature.driver.tripdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmanuelyator.mydistro.core.common.AppError
import com.emmanuelyator.mydistro.core.common.UiState
import com.emmanuelyator.mydistro.core.model.Trip
import com.emmanuelyator.mydistro.feature.driver.domain.TripRepository
import com.emmanuelyator.mydistro.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class TripDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tripRepository: TripRepository
) : ViewModel() {

    private val tripId: String = checkNotNull(savedStateHandle[Routes.Args.TRIP_ID]) {
        "TripDetailsViewModel requires a ${Routes.Args.TRIP_ID} navigation argument"
    }

    /**
     * Observed rather than fetched once, so confirming a delivery on the next
     * screen updates this one when the driver navigates back.
     */
    val uiState: StateFlow<UiState<Trip>> = tripRepository.observeTrip(tripId)
        .map<Trip?, UiState<Trip>> { trip ->
            if (trip == null) {
                UiState.Error(AppError.Validation("This trip is no longer assigned to you."))
            } else {
                UiState.Success(trip)
            }
        }
        .onStart { emit(UiState.Loading) }
        .catch { emit(UiState.Error(AppError.Unknown())) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    private val _confirmDeliveryState = kotlinx.coroutines.flow.MutableStateFlow<UiState<Unit>?>(null)
    val confirmDeliveryState: StateFlow<UiState<Unit>?> = _confirmDeliveryState.asStateFlow()

    fun confirmDelivery(stopId: String, otp: String) {
        viewModelScope.launch {
            _confirmDeliveryState.value = UiState.Loading
            val result = tripRepository.confirmStopDelivery(tripId, stopId, otp)
            when (result) {
                is com.emmanuelyator.mydistro.core.common.DataResult.Success<*> -> {
                    _confirmDeliveryState.value = UiState.Success(Unit)
                }
                is com.emmanuelyator.mydistro.core.common.DataResult.Failure -> {
                    _confirmDeliveryState.value = UiState.Error(result.error)
                }
            }
        }
    }

    fun resetConfirmState() {
        _confirmDeliveryState.value = null
    }
}
