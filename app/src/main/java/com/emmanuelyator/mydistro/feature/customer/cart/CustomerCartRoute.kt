package com.emmanuelyator.mydistro.feature.customer.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroCard
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroPrimaryButton
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroTextField
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroTopBar
import com.emmanuelyator.mydistro.core.designsystem.theme.Dimens
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing
import com.emmanuelyator.mydistro.feature.customer.address.AddDeliveryAddressForm
import com.emmanuelyator.mydistro.feature.customer.address.DeliveryAddressList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerCartRoute(
    viewModel: CustomerCartViewModel = hiltViewModel(),
    onCheckoutSuccess: () -> Unit,
    onAddMoreProducts: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showPaymentSheet by remember { mutableStateOf(false) }
    var showAddressSheet by remember { mutableStateOf(false) }
    var showAddAddress by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }

    if (uiState.paymentMessage != null) {
        showPaymentSheet = false
    }

    CustomerCartScreen(
        uiState = uiState,
        onUpdateQuantity = viewModel::onUpdateQuantity,
        onRemoveProduct = viewModel::onRemoveProduct,
        onCheckout = {
            if (uiState.selectedAddress == null) {
                showAddressSheet = true
                showAddAddress = uiState.addresses.isEmpty()
            } else {
                showPaymentSheet = true
            }
        },
        onChangeAddress = {
            showAddressSheet = true
            showAddAddress = false
        },
        onAddMoreProducts = onAddMoreProducts
    )

    if (showAddressSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showAddressSheet = false
                showAddAddress = false
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.screenPadding)
                    .padding(bottom = 32.dp)
            ) {
                if (showAddAddress) {
                    AddDeliveryAddressForm(
                        isSaving = uiState.isSavingAddress,
                        error = uiState.addressError,
                        onSave = { address ->
                            viewModel.addAddress(address) {
                                showAddAddress = false
                                showAddressSheet = false
                            }
                        }
                    )
                } else {
                    Text(
                        text = "Delivery address",
                        style = MaterialTheme.typography.titleLarge,
                        color = MyDistroTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(Spacing.md))
                    if (uiState.addressesLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else if (uiState.addresses.isEmpty()) {
                        Text(
                            text = uiState.addressError
                                ?: "No saved addresses yet. Add where this order should be delivered.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MyDistroTheme.colors.textSecondary
                        )
                    } else {
                        DeliveryAddressList(
                            addresses = uiState.addresses,
                            selectedId = uiState.selectedAddress?.id,
                            onSelect = {
                                viewModel.selectAddress(it)
                                showAddressSheet = false
                            }
                        )
                    }
                    if (uiState.addressError != null && uiState.addresses.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        Text(
                            text = uiState.addressError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(modifier = Modifier.height(Spacing.lg))
                    MyDistroPrimaryButton(
                        text = "Add new address",
                        onClick = { showAddAddress = true },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    if (showPaymentSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showPaymentSheet = false
                viewModel.resetCheckoutState()
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.screenPadding)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Pay with M-Pesa",
                    style = MaterialTheme.typography.titleLarge,
                    color = MyDistroTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(Spacing.md))
                Text(
                    text = "Enter your M-Pesa phone number. A payment prompt will be sent to your phone. The order leaves the warehouse only after payment succeeds.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MyDistroTheme.colors.textSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(Spacing.md))
                uiState.selectedAddress?.let { address ->
                    Text(
                        text = "Deliver to ${address.label}: ${address.summary}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MyDistroTheme.colors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(Spacing.md))
                }
                MyDistroTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    placeholder = "07XXXXXXXX",
                    label = "Phone Number",
                    keyboardType = KeyboardType.Phone
                )
                Spacer(modifier = Modifier.height(Spacing.xxl))
                if (uiState.isCheckingOut) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(Spacing.md))
                    Text(
                        text = "Waiting for M-Pesa… complete the prompt on your phone.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MyDistroTheme.colors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                } else {
                    MyDistroPrimaryButton(
                        text = "Pay KSh %,d".format(uiState.grandTotal),
                        enabled = phoneNumber.length >= 10 && uiState.canCheckout,
                        onClick = {
                            viewModel.onCheckout(phoneNumber) {
                                onCheckoutSuccess()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (uiState.checkoutError != null) {
                    Spacer(modifier = Modifier.height(Spacing.md))
                    Text(
                        text = uiState.checkoutError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun CustomerCartScreen(
    uiState: CartUiState,
    onUpdateQuantity: (String, Int) -> Unit,
    onRemoveProduct: (String) -> Unit,
    onCheckout: () -> Unit,
    onChangeAddress: () -> Unit,
    onAddMoreProducts: () -> Unit
) {
    Scaffold(
        topBar = {
            MyDistroTopBar(
                title = "Create Order",
                onBack = null
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (uiState.items.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.screenPadding)
                ) {
                    MyDistroPrimaryButton(
                        text = "Checkout - KSh %,d".format(uiState.grandTotal),
                        onClick = onCheckout,
                        enabled = !uiState.isCheckingOut,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    ) { padding ->
        if (uiState.items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Your cart is empty.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MyDistroTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(Spacing.md))
                    TextButton(onClick = onAddMoreProducts) {
                        Text("Browse Products", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = Dimens.screenPadding)
            ) {
                item {
                    SectionHeader(title = "Order Items", actionText = "Add more products", onAction = onAddMoreProducts)
                    Spacer(modifier = Modifier.height(Spacing.sm))
                }

                items(uiState.items, key = { it.product.id }) { item ->
                    CartItemCard(
                        item = item,
                        onUpdateQuantity = { qty -> onUpdateQuantity(item.product.id, qty) },
                        onRemove = { onRemoveProduct(item.product.id) },
                        modifier = Modifier.padding(horizontal = Dimens.screenPadding, vertical = Spacing.xs)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(Spacing.md))
                    AddProductButton(onClick = onAddMoreProducts)
                    Spacer(modifier = Modifier.height(Spacing.xl))
                }

                item {
                    SectionHeader(
                        title = "Delivery Address",
                        actionText = if (uiState.addresses.isEmpty()) "Add" else "Change",
                        onAction = onChangeAddress
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    MyDistroCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens.screenPadding)
                            .clickable(onClick = onChangeAddress),
                        contentPadding = Spacing.lg
                    ) {
                        val selected = uiState.selectedAddress
                        when {
                            uiState.addressesLoading && selected == null -> {
                                Text(
                                    text = "Loading addresses…",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MyDistroTheme.colors.textSecondary
                                )
                            }
                            selected != null -> {
                                Text(
                                    text = selected.label,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                                    color = MyDistroTheme.colors.textPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = selected.summary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MyDistroTheme.colors.textSecondary
                                )
                            }
                            else -> {
                                Text(
                                    text = uiState.addressError
                                        ?: "Tap to choose or add a delivery address",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MyDistroTheme.colors.textSecondary
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(Spacing.xl))
                }

                item {
                    SectionHeader(title = "Payment Method", actionText = null, onAction = null)
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    MyDistroCard(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.screenPadding),
                        contentPadding = Spacing.lg
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF25D366)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Payments,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(Spacing.md))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "M-Pesa (via Daraja)",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MyDistroTheme.colors.textPrimary
                                )
                                Text(
                                    text = "Order leaves only after payment succeeds",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MyDistroTheme.colors.textSecondary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, actionText: String?, onAction: (() -> Unit)?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.screenPadding, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MyDistroTheme.colors.textPrimary
        )
        if (actionText != null && onAction != null) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(onClick = onAction).padding(Spacing.xs)
            )
        }
    }
}

@Composable
private fun CartItemCard(
    item: CartItemUiModel,
    onUpdateQuantity: (Int) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    MyDistroCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = Spacing.md
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MyDistroTheme.colors.neutralContainer),
                contentAlignment = Alignment.Center
            ) {
                if (item.product.imageUrl != null) {
                    AsyncImage(
                        model = item.product.imageUrl,
                        contentDescription = item.product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.width(Spacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${item.product.name} ${item.product.unitLabel}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MyDistroTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "KSh %,d".format(item.product.priceKes),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MyDistroTheme.colors.textPrimary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = "Remove",
                        tint = MyDistroTheme.colors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(Spacing.sm))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MyDistroTheme.colors.neutralContainer)
                ) {
                    IconButton(
                        onClick = { onUpdateQuantity(item.quantity - 1) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Outlined.Remove, contentDescription = "-", modifier = Modifier.size(16.dp))
                    }
                    Text(
                        text = item.quantity.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = Spacing.xs)
                    )
                    IconButton(
                        onClick = { onUpdateQuantity(item.quantity + 1) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = "+", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AddProductButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.screenPadding)
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MyDistroTheme.colors.border, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(Spacing.xs))
            Text(
                text = "Add Product",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
