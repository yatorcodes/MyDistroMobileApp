package com.emmanuelyator.mydistro.feature.customer.address

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroCard
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroPrimaryButton
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroTextField
import com.emmanuelyator.mydistro.core.designsystem.theme.Dimens
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing
import com.emmanuelyator.mydistro.core.model.DeliveryAddress

@Composable
fun DeliveryAddressList(
    addresses: List<DeliveryAddress>,
    selectedId: String?,
    onSelect: (DeliveryAddress) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        addresses.forEach { address ->
            val selected = address.id != null && address.id == selectedId
            MyDistroCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(address) },
                contentPadding = Spacing.md
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = if (selected) Icons.Outlined.CheckCircle else Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MyDistroTheme.colors.textSecondary
                        }
                    )
                    Spacer(modifier = Modifier.padding(horizontal = Spacing.sm))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = address.label,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MyDistroTheme.colors.textPrimary
                            )
                            if (address.isDefault) {
                                Text(
                                    text = "  DEFAULT",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = address.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MyDistroTheme.colors.textSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddDeliveryAddressForm(
    isSaving: Boolean,
    error: String?,
    defaultRecipientName: String = "",
    defaultPhoneNumber: String = "",
    onSave: (DeliveryAddress) -> Unit,
    modifier: Modifier = Modifier
) {
    var label by remember { mutableStateOf("") }
    var recipientName by remember { mutableStateOf(defaultRecipientName) }
    var phoneNumber by remember { mutableStateOf(defaultPhoneNumber) }
    var addressLine by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var county by remember { mutableStateOf("") }
    var makeDefault by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "New delivery address",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MyDistroTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(Spacing.md))
        MyDistroTextField(
            value = label,
            onValueChange = { label = it },
            label = "Label",
            placeholder = "Main shop"
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        MyDistroTextField(
            value = recipientName,
            onValueChange = { recipientName = it },
            label = "Recipient name",
            placeholder = "Who receives the delivery"
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        MyDistroTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = "Phone",
            placeholder = "07XXXXXXXX",
            keyboardType = KeyboardType.Phone
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        MyDistroTextField(
            value = addressLine,
            onValueChange = { addressLine = it },
            label = "Address",
            placeholder = "Street, building, landmark"
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        MyDistroTextField(
            value = city,
            onValueChange = { city = it },
            label = "City (optional)",
            placeholder = "Nairobi"
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        MyDistroTextField(
            value = county,
            onValueChange = { county = it },
            label = "County (optional)",
            placeholder = "Nairobi"
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = makeDefault, onCheckedChange = { makeDefault = it })
            Text(
                text = "Use as default address",
                style = MaterialTheme.typography.bodyMedium,
                color = MyDistroTheme.colors.textPrimary
            )
        }
        val shownError = localError ?: error
        if (shownError != null) {
            Text(
                text = shownError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
        }
        MyDistroPrimaryButton(
            text = if (isSaving) "Saving…" else "Save address",
            enabled = !isSaving,
            onClick = {
                localError = when {
                    label.trim().isEmpty() -> "Enter a short label for this place."
                    recipientName.trim().isEmpty() -> "Enter the recipient's name."
                    phoneNumber.trim().length < 10 -> "Enter a valid phone number."
                    addressLine.trim().isEmpty() -> "Enter the street address."
                    else -> null
                }
                if (localError != null) return@MyDistroPrimaryButton
                onSave(
                    DeliveryAddress(
                        id = null,
                        label = label.trim(),
                        recipientName = recipientName.trim(),
                        phoneNumber = phoneNumber.trim(),
                        addressLine = addressLine.trim(),
                        city = city.trim(),
                        county = county.trim(),
                        isDefault = makeDefault
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(Dimens.screenPadding))
    }
}
