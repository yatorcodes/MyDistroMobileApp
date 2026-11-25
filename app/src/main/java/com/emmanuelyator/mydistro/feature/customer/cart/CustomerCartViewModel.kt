package com.emmanuelyator.mydistro.feature.customer.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmanuelyator.mydistro.core.common.DataResult
import com.emmanuelyator.mydistro.core.model.DeliveryAddress
import com.emmanuelyator.mydistro.core.model.Product
import com.emmanuelyator.mydistro.feature.customer.domain.AddressRepository
import com.emmanuelyator.mydistro.feature.customer.domain.CartRepository
import com.emmanuelyator.mydistro.feature.customer.domain.CatalogRepository
import com.emmanuelyator.mydistro.feature.customer.domain.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State: Presentation model joining domain [Product] with cart quantity and computed line item cost.
 */
data class CartItemUiModel(
    val product: Product,
    val quantity: Int
) {
    val totalCost: Int get() = product.priceKes * quantity
}

/**
 * UI State: cart contents, delivery address, and checkout progress.
 */
data class CartUiState(
    val items: List<CartItemUiModel> = emptyList(),
    val addresses: List<DeliveryAddress> = emptyList(),
    val selectedAddress: DeliveryAddress? = null,
    val addressesLoading: Boolean = false,
    val addressError: String? = null,
    val isSavingAddress: Boolean = false,
    val isCheckingOut: Boolean = false,
    val checkoutError: String? = null,
    val paymentMessage: String? = null
) {
    val grandTotal: Int get() = items.sumOf { it.totalCost }
    val canCheckout: Boolean = items.isNotEmpty() && selectedAddress != null && !isCheckingOut
}

/**
 * Aggregates cart quantities with catalogue products, saved delivery addresses, and checkout.
 */
@HiltViewModel
class CustomerCartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    catalogRepository: CatalogRepository,
    private val orderRepository: OrderRepository,
    private val addressRepository: AddressRepository
) : ViewModel() {

    private val checkout = CustomerCheckout(orderRepository)

    private val checkoutState = MutableStateFlow(CartUiState())

    /** Reused while the same payment attempt is in flight; cleared after paid or a hard failure. */
    private var paymentIdempotencyKey: String? = null

    val uiState: StateFlow<CartUiState> = combine(
        cartRepository.quantities,
        catalogRepository.observeProducts(null, ""),
        checkoutState
    ) { quantities, allProducts, checkout ->
        val items = quantities.mapNotNull { (productId, qty) ->
            val product = allProducts.find { it.id == productId }
            if (product != null && qty > 0) CartItemUiModel(product, qty) else null
        }
        // Items come from the cart; address and payment flags from checkoutState.
        checkout.copy(items = items)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CartUiState()
    )

    init {
        refreshAddresses()
    }

    fun refreshAddresses() {
        viewModelScope.launch {
            checkoutState.update {
                it.copy(addressesLoading = true, addressError = null)
            }
            when (val result = addressRepository.list()) {
                is DataResult.Success -> {
                    val addresses = result.data
                    val selected = checkoutState.value.selectedAddress
                        ?.let { current -> addresses.firstOrNull { it.id == current.id } }
                        ?: addresses.firstOrNull { it.isDefault }
                        ?: addresses.firstOrNull()
                    checkoutState.update {
                        it.copy(
                            addresses = addresses,
                            selectedAddress = selected,
                            addressesLoading = false,
                            addressError = if (addresses.isEmpty()) {
                                "Add a delivery address before checkout."
                            } else {
                                null
                            }
                        )
                    }
                }
                is DataResult.Failure -> {
                    checkoutState.update {
                        it.copy(
                            addressesLoading = false,
                            addressError = result.error.message
                        )
                    }
                }
            }
        }
    }

    fun selectAddress(address: DeliveryAddress) {
        checkoutState.update {
            it.copy(selectedAddress = address, addressError = null, checkoutError = null)
        }
    }

    fun addAddress(address: DeliveryAddress, onSaved: (() -> Unit)? = null) {
        if (checkoutState.value.isSavingAddress) return
        viewModelScope.launch {
            checkoutState.update { it.copy(isSavingAddress = true, addressError = null) }
            when (val result = addressRepository.add(address)) {
                is DataResult.Success -> {
                    val saved = result.data
                    val addresses = checkoutState.value.addresses
                        .map { if (saved.isDefault) it.copy(isDefault = false) else it }
                        .filterNot { it.id != null && it.id == saved.id } + saved
                    checkoutState.update {
                        it.copy(
                            addresses = addresses,
                            selectedAddress = saved,
                            isSavingAddress = false,
                            addressError = null
                        )
                    }
                    onSaved?.invoke()
                }
                is DataResult.Failure -> {
                    checkoutState.update {
                        it.copy(isSavingAddress = false, addressError = result.error.message)
                    }
                }
            }
        }
    }

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
        if (checkoutState.value.isCheckingOut) return
        viewModelScope.launch {
            checkoutState.update {
                it.copy(isCheckingOut = true, checkoutError = null, paymentMessage = null)
            }
            val items = uiState.value.items
            val address = checkoutState.value.selectedAddress
            val (result, key) = checkout.checkout(
                items = items,
                address = address,
                phoneNumber = phoneNumber,
                idempotencyKey = paymentIdempotencyKey
            )
            paymentIdempotencyKey = key
            when (result) {
                is CheckoutResult.Paid -> {
                    paymentIdempotencyKey = null
                    cartRepository.clear()
                    checkoutState.update {
                        it.copy(
                            isCheckingOut = false,
                            paymentMessage = result.message,
                            checkoutError = null
                        )
                    }
                    onComplete()
                }
                is CheckoutResult.Failed -> {
                    // Keep the key so a retry of the same attempt stays idempotent until paid.
                    checkoutState.update {
                        it.copy(
                            isCheckingOut = false,
                            checkoutError = result.message,
                            paymentMessage = null
                        )
                    }
                }
            }
        }
    }

    fun resetCheckoutState() {
        checkoutState.update {
            it.copy(checkoutError = null, paymentMessage = null, addressError = null)
        }
    }
}
