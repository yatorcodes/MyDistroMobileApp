package com.emmanuelyator.mydistro.feature.customer.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroTopBar
import com.emmanuelyator.mydistro.core.designsystem.component.OrderStatusBadge
import com.emmanuelyator.mydistro.core.designsystem.component.label
import com.emmanuelyator.mydistro.core.designsystem.theme.Dimens
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing
import com.emmanuelyator.mydistro.core.model.Order
import com.emmanuelyator.mydistro.core.model.OrderStatus
import com.emmanuelyator.mydistro.core.model.OrderStatusTimeline
import com.emmanuelyator.mydistro.feature.customer.data.MockOrderData
import java.time.format.DateTimeFormatter

@Composable
fun CustomerOrderStatusRoute(
    orderId: String,
    onBack: () -> Unit
) {
    val order = MockOrderData.orders.find { it.id == orderId }
    
    if (order != null) {
        CustomerOrderStatusScreen(order = order, onBack = onBack)
    } else {
        Scaffold(
            topBar = { MyDistroTopBar(title = "Order Status", onBack = onBack) },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Order not found")
            }
        }
    }
}

@Composable
fun CustomerOrderStatusScreen(
    order: Order,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            MyDistroTopBar(
                title = "Order Status",
                onBack = onBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.screenPadding, vertical = Spacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${order.id}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MyDistroTheme.colors.heroSurface
                )
                OrderStatusBadge(status = order.status)
            }
            
            Spacer(modifier = Modifier.height(Spacing.lg))
            
            // Timeline
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.screenPadding)
            ) {
                // For a real app, you'd match the order's status history
                val currentIndex = OrderStatusTimeline.indexOf(order.status).takeIf { it >= 0 } ?: OrderStatusTimeline.size
                
                OrderStatusTimeline.forEachIndexed { index, status ->
                    val isCompleted = index < currentIndex
                    val isCurrent = index == currentIndex
                    val isPending = index > currentIndex
                    
                    TimelineNode(
                        status = status,
                        isCompleted = isCompleted,
                        isCurrent = isCurrent,
                        isPending = isPending,
                        isLast = index == OrderStatusTimeline.lastIndex,
                        timestamp = if (isCompleted || isCurrent) order.date.format(DateTimeFormatter.ofPattern("MMM d, yyyy • h:mm a")) else null
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(Spacing.xl))
            
            // Info Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.screenPadding)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MyDistroTheme.colors.infoContainer)
                    .padding(Spacing.lg)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Outlined.LocalShipping,
                        contentDescription = null,
                        tint = MyDistroTheme.colors.info,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.md))
                    Text(
                        text = "Your order is confirmed! Payment will be unlocked once distributor confirms stock.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MyDistroTheme.colors.info
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineNode(
    status: OrderStatus,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isPending: Boolean,
    isLast: Boolean,
    timestamp: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
    ) {
        // Left Column (Icon + Line)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted -> MyDistroTheme.colors.success
                            isCurrent -> MyDistroTheme.colors.warning
                            else -> Color.Transparent
                        }
                    )
                    .border(
                        width = 2.dp,
                        color = when {
                            isCompleted -> MyDistroTheme.colors.success
                            isCurrent -> MyDistroTheme.colors.warning
                            else -> MyDistroTheme.colors.border
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                } else if (isCurrent) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }
            
            // Line
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .padding(vertical = 4.dp)
                        .background(
                            if (isCompleted && !isCurrent) MyDistroTheme.colors.success 
                            else if (isCurrent) MyDistroTheme.colors.warning 
                            else MyDistroTheme.colors.border
                        )
                )
            }
        }
        
        Spacer(modifier = Modifier.width(Spacing.md))
        
        // Right Column (Text)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 16.dp) // space below text
        ) {
            Text(
                text = status.label,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (isPending) MyDistroTheme.colors.textSecondary else MyDistroTheme.colors.textPrimary
            )
            
            if (timestamp != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = timestamp,
                    style = MaterialTheme.typography.bodySmall,
                    color = MyDistroTheme.colors.textTertiary
                )
            }
            
            if (isCurrent && status == OrderStatus.PAYMENT_PENDING) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Awaiting M-Pesa payment",
                    style = MaterialTheme.typography.bodySmall,
                    color = MyDistroTheme.colors.warning
                )
            }
        }
    }
}
