package com.emmanuelyator.mydistro.feature.driver.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmanuelyator.mydistro.core.common.AppError
import com.emmanuelyator.mydistro.core.common.DataResult
import com.emmanuelyator.mydistro.core.common.UiState
import com.emmanuelyator.mydistro.core.model.Driver
import com.emmanuelyator.mydistro.core.model.Trip
import com.emmanuelyator.mydistro.feature.auth.domain.AuthRepository
import com.emmanuelyator.mydistro.feature.driver.domain.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TripsTab { ACTIVE, HISTORY }

data class DriverTripsUiState(
    val driver: Driver? = null,
    val selectedTab: TripsTab = TripsTab.ACTIVE,
    val activeTrips: UiState<List<Trip>> = UiState.Loading,
    val historyTrips: UiState<List<Trip>> = UiState.Loading,
    val isRefreshing: Boolean = false,
    /**
     * Failure of a *refresh*, as opposed to a failure to load at all. Shown as a
     * banner over the existing list instead of replacing it, so a driver never
     * loses sight of their route because the network blipped.
     */
    val refreshError: String? = null
) {
    val visibleTrips: UiState<List<Trip>>
        get() = when (selectedTab) {
            TripsTab.ACTIVE -> activeTrips
            TripsTab.HISTORY -> historyTrips
        }
}

@HiltViewModel
class DriverTripsViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val selectedTab = MutableStateFlow(TripsTab.ACTIVE)
    private val driver = MutableStateFlow<Driver?>(null)
    private val refreshing = MutableStateFlow(false)
    private val refreshError = MutableStateFlow<String?>(null)

    private val activeTrips = tripRepository.observeActiveTrips()
        .asListUiState()
    private val historyTrips = tripRepository.observeTripHistory()
        .asListUiState()

    val uiState: StateFlow<DriverTripsUiState> =
        combine(
            driver,
            selectedTab,
            activeTrips,
            historyTrips,
            // combine tops out at five flows before needing the vararg form, so
            // the two refresh signals are paired up first.
            combine(refreshing, refreshError) { isRefreshing, error -> isRefreshing to error }
        ) { currentDriver, tab, active, history, refreshState ->
            DriverTripsUiState(
                driver = currentDriver,
                selectedTab = tab,
                activeTrips = active,
                historyTrips = history,
                isRefreshing = refreshState.first,
                refreshError = refreshState.second
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DriverTripsUiState()
        )

    init {
        loadDriver()
        refresh()
    }

    fun onTabSelected(tab: TripsTab) {
        selectedTab.value = tab
    }

    fun refresh() {
        if (refreshing.value) return
        refreshing.value = true
        refreshError.value = null

        viewModelScope.launch {
            when (val result = tripRepository.refresh()) {
                is DataResult.Success -> refreshError.value = null
                is DataResult.Failure -> refreshError.value = result.error.message
            }
            refreshing.value = false
        }
    }

    fun onRefreshErrorDismissed() {
        refreshError.value = null
    }

    private fun loadDriver() {
        viewModelScope.launch {
            when (val result = authRepository.currentDriver()) {
                is DataResult.Success -> driver.value = result.data
                // The dashboard degrades to a generic greeting rather than
                // blocking the trip list, which is what the driver came for.
                is DataResult.Failure -> driver.value = null
            }
        }
    }
}

/**
 * Turns a stream of trips into the four-state model the UI renders, mapping an
 * empty list to [UiState.Empty] so screens cannot silently show a blank list.
 */
private fun Flow<List<Trip>>.asListUiState(): Flow<UiState<List<Trip>>> =
    map<List<Trip>, UiState<List<Trip>>> { trips ->
        if (trips.isEmpty()) UiState.Empty else UiState.Success(trips)
    }
        .onStart { emit(UiState.Loading) }
        .catch { emit(UiState.Error(AppError.Unknown())) }
