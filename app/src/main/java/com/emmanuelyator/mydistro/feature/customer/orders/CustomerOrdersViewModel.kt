package com.emmanuelyator.mydistro.feature.customer.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmanuelyator.mydistro.core.common.DataResult
import com.emmanuelyator.mydistro.core.model.Order
import com.emmanuelyator.mydistro.feature.customer.domain.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CustomerOrdersUiState(
    val orders: List<Order> = emptyList(),
    val loading: Boolean = true,
    val refreshing: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class CustomerOrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
) : ViewModel() {

    private val loading = MutableStateFlow(true)
    private val refreshing = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<CustomerOrdersUiState> = combine(
        orderRepository.observeOrders(),
        loading,
        refreshing,
        errorMessage,
    ) { orders, isLoading, isRefreshing, error ->
        CustomerOrdersUiState(
            orders = orders,
            loading = isLoading && orders.isEmpty(),
            refreshing = isRefreshing,
            errorMessage = error,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CustomerOrdersUiState(),
    )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val hasOrders = uiState.value.orders.isNotEmpty()
            if (hasOrders) {
                refreshing.value = true
            } else {
                loading.value = true
            }
            errorMessage.value = null
            when (val result = orderRepository.refreshOrders()) {
                is DataResult.Success -> errorMessage.value = null
                is DataResult.Failure -> errorMessage.value = result.error.message
            }
            loading.value = false
            refreshing.value = false
        }
    }
}
