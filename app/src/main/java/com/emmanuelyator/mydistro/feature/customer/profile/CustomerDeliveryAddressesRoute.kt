package com.emmanuelyator.mydistro.feature.customer.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroCard
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroPrimaryButton
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroTopBar
import com.emmanuelyator.mydistro.core.designsystem.theme.Dimens
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing
import com.emmanuelyator.mydistro.core.model.DeliveryAddress
import com.emmanuelyator.mydistro.feature.customer.address.AddDeliveryAddressForm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDeliveryAddressesRoute(
    onBack: () -> Unit,
    viewModel: DeliveryAddressesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CustomerDeliveryAddressesScreen(
        uiState = uiState,
        onBack = onBack,
        onAddNew = { viewModel.showAddForm(true) }
    )

    if (uiState.showAddForm) {
        ModalBottomSheet(onDismissRequest = { viewModel.showAddForm(false) }) {
            AddDeliveryAddressForm(
                isSaving = uiState.isSaving,
                error = uiState.error,
                onSave = viewModel::addAddress,
                modifier = Modifier.padding(horizontal = Dimens.screenPadding)
            )
        }
    }
}

@Composable
fun CustomerDeliveryAddressesScreen(
    uiState: DeliveryAddressesUiState,
    onBack: () -> Unit,
    onAddNew: () -> Unit
) {
    Scaffold(
        topBar = {
            MyDistroTopBar(
                title = "Delivery Addresses",
                onBack = onBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Box(modifier = Modifier.padding(Dimens.screenPadding)) {
                MyDistroPrimaryButton(
                    text = "Add New Address",
                    onClick = onAddNew,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { padding ->
        when {
            uiState.isLoading && uiState.addresses.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = Dimens.screenPadding, vertical = Spacing.lg)
                ) {
                    val loadError = uiState.error
                    if (loadError != null && uiState.addresses.isEmpty()) {
                        Text(
                            text = loadError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else if (uiState.addresses.isEmpty()) {
                        Text(
                            text = "No saved addresses yet. Add the shops or places where you receive deliveries.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MyDistroTheme.colors.textSecondary
                        )
                    } else {
                        uiState.addresses.forEachIndexed { index, address ->
                            if (index > 0) Spacer(modifier = Modifier.height(Spacing.md))
                            AddressCard(address = address)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddressCard(address: DeliveryAddress) {
    MyDistroCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = Spacing.md
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = if (address.isDefault) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MyDistroTheme.colors.textSecondary
                },
                modifier = Modifier.size(24.dp).padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.width(Spacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = address.label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MyDistroTheme.colors.textPrimary
                    )

                    if (address.isDefault) {
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        Text(
                            text = "DEFAULT",
                            style = MaterialTheme.typography.labelSmall,
                            color = androidx.compose.ui.graphics.Color.White,
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xs))

                Text(
                    text = address.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MyDistroTheme.colors.textSecondary
                )
                if (address.recipientName.isNotBlank()) {
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = "${address.recipientName} · ${address.phoneNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MyDistroTheme.colors.textSecondary
                    )
                }
            }
        }
    }
}
