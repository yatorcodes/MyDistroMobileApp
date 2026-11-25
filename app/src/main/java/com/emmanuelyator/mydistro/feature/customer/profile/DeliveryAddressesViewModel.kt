package com.emmanuelyator.mydistro.feature.customer.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmanuelyator.mydistro.core.common.DataResult
import com.emmanuelyator.mydistro.core.model.DeliveryAddress
import com.emmanuelyator.mydistro.feature.customer.domain.AddressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DeliveryAddressesUiState(
    val addresses: List<DeliveryAddress> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val showAddForm: Boolean = false
)

@HiltViewModel
class DeliveryAddressesViewModel @Inject constructor(
    private val addressRepository: AddressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeliveryAddressesUiState())
    val uiState: StateFlow<DeliveryAddressesUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = addressRepository.list()) {
                is DataResult.Success ->
                    _uiState.update {
                        it.copy(addresses = result.data, isLoading = false, error = null)
                    }
                is DataResult.Failure ->
                    _uiState.update {
                        it.copy(isLoading = false, error = result.error.message)
                    }
            }
        }
    }

    fun showAddForm(show: Boolean) {
        _uiState.update { it.copy(showAddForm = show, error = null) }
    }

    fun addAddress(address: DeliveryAddress) {
        if (_uiState.value.isSaving) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            when (val result = addressRepository.add(address)) {
                is DataResult.Success -> {
                    refresh()
                    _uiState.update { it.copy(isSaving = false, showAddForm = false) }
                }
                is DataResult.Failure ->
                    _uiState.update {
                        it.copy(isSaving = false, error = result.error.message)
                    }
            }
        }
    }
}
