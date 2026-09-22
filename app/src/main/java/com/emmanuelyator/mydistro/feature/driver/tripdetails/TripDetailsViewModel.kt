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

@HiltViewModel
class TripDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    tripRepository: TripRepository
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
}
