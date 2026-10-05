package com.emmanuelyator.mydistro.feature.customer.orders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroCard
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroTopBar
import com.emmanuelyator.mydistro.core.designsystem.component.OrderStatusBadge
import com.emmanuelyator.mydistro.core.designsystem.theme.Dimens
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing
import com.emmanuelyator.mydistro.core.model.Order
import com.emmanuelyator.mydistro.feature.customer.data.MockOrderData
import java.time.format.DateTimeFormatter

@Composable
fun CustomerOrdersRoute(
    onOrderClick: (String) -> Unit
) {
    CustomerOrdersScreen(
        orders = MockOrderData.orders,
        onOrderClick = onOrderClick
    )
}

@Composable
fun CustomerOrdersScreen(
    orders: List<Order>,
    onOrderClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            MyDistroTopBar(
                title = "Order History",
                onBack = null, // Bottom nav screen, no back button
                actions = {
                    IconButton(onClick = { /* Open filter */ }) {
                        Icon(
                            imageVector = Icons.Outlined.FilterAlt,
                            contentDescription = "Filter Orders",
                            tint = MyDistroTheme.colors.textPrimary
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(vertical = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            items(orders, key = { it.id }) { order ->
                OrderCard(
                    order = order,
                    onClick = { onOrderClick(order.id) },
                    modifier = Modifier.padding(horizontal = Dimens.screenPadding)
                )
            }
        }
    }
}

@Composable
private fun OrderCard(
    order: Order,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy")
    
    MyDistroCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        contentPadding = Spacing.lg
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#${order.id}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MyDistroTheme.colors.heroSurface
            )
            OrderStatusBadge(status = order.status)
        }
        
        Spacer(modifier = Modifier.height(Spacing.sm))
        
        Text(
            text = order.itemSummary,
            style = MaterialTheme.typography.bodyLarge,
            color = MyDistroTheme.colors.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        Spacer(modifier = Modifier.height(Spacing.md))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = order.totalLabel,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MyDistroTheme.colors.textPrimary
            )
            Text(
                text = order.date.format(formatter),
                style = MaterialTheme.typography.bodyMedium,
                color = MyDistroTheme.colors.textTertiary
            )
        }
    }
}
