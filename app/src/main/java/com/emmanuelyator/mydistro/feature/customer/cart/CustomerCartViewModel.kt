package com.emmanuelyator.mydistro.feature.customer.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmanuelyator.mydistro.core.model.Product
import com.emmanuelyator.mydistro.feature.customer.domain.CartRepository
import com.emmanuelyator.mydistro.feature.customer.domain.CatalogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.emmanuelyator.mydistro.feature.customer.data.CustomerApiService
import com.emmanuelyator.mydistro.feature.customer.data.OrderItemRequest
import com.emmanuelyator.mydistro.feature.customer.data.OrderRequest
import com.emmanuelyator.mydistro.feature.customer.data.PaymentInitiateRequest

data class CartItemUiModel(
    val product: Product,
    val quantity: Int
) {
    val totalCost: Int get() = product.priceKes * quantity
}

data class CartUiState(
    val items: List<CartItemUiModel> = emptyList(),
    val isCheckingOut: Boolean = false,
    val checkoutError: String? = null,
    val paymentMessage: String? = null
) {
    val grandTotal: Int get() = items.sumOf { it.totalCost }
}

@HiltViewModel
class CustomerCartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    catalogRepository: CatalogRepository,
    private val apiService: CustomerApiService
) : ViewModel() {

    private val _checkoutState = kotlinx.coroutines.flow.MutableStateFlow(
        CartUiState(isCheckingOut = false, checkoutError = null, paymentMessage = null)
    )

    val uiState: StateFlow<CartUiState> = combine(
        cartRepository.quantities,
        catalogRepository.observeProducts(null, ""),
        _checkoutState
    ) { quantities, allProducts, checkoutState ->
        val items = quantities.mapNotNull { (productId, qty) ->
            val product = allProducts.find { it.id == productId }
            if (product != null && qty > 0) {
                CartItemUiModel(product, qty)
            } else null
        }
        CartUiState(
            items = items,
            isCheckingOut = checkoutState.isCheckingOut,
            checkoutError = checkoutState.checkoutError,
            paymentMessage = checkoutState.paymentMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CartUiState()
    )

    fun onUpdateQuantity(productId: String, newQuantity: Int) {
        viewModelScope.launch {
            cartRepository.updateQuantity(productId, newQuantity)
        }
    }

    fun onRemoveProduct(productId: String) {
        viewModelScope.launch {
            cartRepository.removeProduct(productId)
        }
    }
    
    fun onCheckout(phoneNumber: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            _checkoutState.value = _checkoutState.value.copy(isCheckingOut = true, checkoutError = null, paymentMessage = null)
            
            try {
                // 1. Create Order
                val currentItems = uiState.value.items
                val orderRequest = OrderRequest(
                    organizationId = "ff8f0186-1ca8-4657-82f3-ba659d94d93f",
                    deliveryAddress = "User Default Address", // Hardcoded for demo
                    items = currentItems.map { OrderItemRequest(it.product.id, it.quantity) }
                )
                
                val orderResponse = apiService.createOrder(orderRequest)
                if (!orderResponse.isSuccessful || orderResponse.body() == null) {
                    _checkoutState.value = _checkoutState.value.copy(
                        isCheckingOut = false,
                        checkoutError = "Failed to create order: ${orderResponse.code()}"
                    )
                    return@launch
                }
                
                val orderId = orderResponse.body()!!.id
                
                // 2. Initiate Payment
                val paymentReq = PaymentInitiateRequest(orderId, phoneNumber)
                val payResponse = apiService.initiatePayment(paymentReq)
                
                if (payResponse.isSuccessful && payResponse.body() != null) {
                    val msg = payResponse.body()!!.customerMessage
                    _checkoutState.value = _checkoutState.value.copy(
                        isCheckingOut = false,
                        paymentMessage = msg
                    )
                    cartRepository.clear()
                    onComplete()
                } else {
                    _checkoutState.value = _checkoutState.value.copy(
                        isCheckingOut = false,
                        checkoutError = "Payment failed: ${payResponse.code()}"
                    )
                }
                
            } catch (e: Exception) {
                _checkoutState.value = _checkoutState.value.copy(
                    isCheckingOut = false,
                    checkoutError = e.message ?: "Connection error"
                )
            }
        }
    }
    
    fun resetCheckoutState() {
        _checkoutState.value = _checkoutState.value.copy(checkoutError = null, paymentMessage = null)
    }
}
