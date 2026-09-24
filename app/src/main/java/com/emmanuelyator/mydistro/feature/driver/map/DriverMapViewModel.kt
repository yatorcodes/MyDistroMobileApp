package com.emmanuelyator.mydistro.feature.driver.map

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmanuelyator.mydistro.core.model.Trip
import com.emmanuelyator.mydistro.feature.driver.domain.TripRepository
import com.emmanuelyator.mydistro.navigation.Routes
import com.mapbox.geojson.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriverMapViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val tripRepository: TripRepository
) : ViewModel() {

    private val _activeTrip = MutableStateFlow<Trip?>(null)
    val activeTrip: StateFlow<Trip?> = _activeTrip.asStateFlow()

    private val _routePoints = MutableStateFlow<List<Point>>(emptyList())
    val routePoints: StateFlow<List<Point>> = _routePoints.asStateFlow()

    init {
        loadActiveTrip()
    }

    private fun loadActiveTrip() {
        val requestedTripId = savedStateHandle.get<String>(Routes.Args.TRIP_ID)

        viewModelScope.launch {
            if (requestedTripId != null) {
                tripRepository.observeTrip(requestedTripId).collect { trip ->
                    _activeTrip.value = trip
                }
            } else {
                tripRepository.observeActiveTrips().collect { trips ->
                    val trip = trips.firstOrNull { it.status == com.emmanuelyator.mydistro.core.model.TripStatus.ONGOING }
                        ?: trips.firstOrNull { it.status == com.emmanuelyator.mydistro.core.model.TripStatus.SCHEDULED }
                    _activeTrip.value = trip
                }
            }
        }
    }

    fun fetchRoute(waypoints: List<Point>, token: String) {
        viewModelScope.launch {
            val points = RouteFetcher.fetchRoute(waypoints, token)
            if (points != null) {
                _routePoints.value = points
            }
        }
    }
}
