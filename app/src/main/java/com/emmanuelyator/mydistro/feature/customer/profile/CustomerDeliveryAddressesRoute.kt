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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroCard
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroPrimaryButton
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroTopBar
import com.emmanuelyator.mydistro.core.designsystem.theme.Dimens
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing

@Composable
fun CustomerDeliveryAddressesRoute(
    onBack: () -> Unit
) {
    CustomerDeliveryAddressesScreen(onBack = onBack)
}

@Composable
fun CustomerDeliveryAddressesScreen(
    onBack: () -> Unit
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
                    onClick = { /* TODO: Open add address sheet */ },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.screenPadding, vertical = Spacing.lg)
        ) {
            AddressCard(
                title = "Main Shop",
                address = "Jane's Mini Mart\nNairobi CBD, Moi Avenue",
                isDefault = true
            )
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            AddressCard(
                title = "Branch 2",
                address = "Jane's Groceries\nWestlands, Woodvale Grove",
                isDefault = false
            )
        }
    }
}

@Composable
private fun AddressCard(
    title: String,
    address: String,
    isDefault: Boolean
) {
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
                tint = if (isDefault) MaterialTheme.colorScheme.primary else MyDistroTheme.colors.textSecondary,
                modifier = Modifier.size(24.dp).padding(top = 2.dp)
            )
            
            Spacer(modifier = Modifier.width(Spacing.md))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MyDistroTheme.colors.textPrimary
                    )
                    
                    if (isDefault) {
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
                    text = address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MyDistroTheme.colors.textSecondary
                )
            }
            
            Column {
                IconButton(onClick = { /* TODO */ }, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit",
                        tint = MyDistroTheme.colors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                if (!isDefault) {
                    IconButton(onClick = { /* TODO */ }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
