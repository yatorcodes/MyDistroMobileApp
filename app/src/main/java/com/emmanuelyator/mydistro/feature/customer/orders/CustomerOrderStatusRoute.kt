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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroTopBar
import com.emmanuelyator.mydistro.core.designsystem.component.OrderStatusBadge
import com.emmanuelyator.mydistro.core.designsystem.component.label
import com.emmanuelyator.mydistro.core.designsystem.theme.Dimens
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing
import com.emmanuelyator.mydistro.core.model.Order
import com.emmanuelyator.mydistro.core.model.OrderStatus
import com.emmanuelyator.mydistro.core.model.OrderStatusTimeline
import java.time.format.DateTimeFormatter

@Composable
fun CustomerOrderStatusRoute(
    orderId: String,
    onBack: () -> Unit,
    viewModel: CustomerOrderStatusViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading && uiState.order == null -> {
            Scaffold(
                topBar = { MyDistroTopBar(title = "Order Status", onBack = onBack) },
                containerColor = MaterialTheme.colorScheme.background
            ) { padding ->
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        uiState.order == null -> {
            Scaffold(
                topBar = { MyDistroTopBar(title = "Order Status", onBack = onBack) },
                containerColor = MaterialTheme.colorScheme.background
            ) { padding ->
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(uiState.error ?: "Order not found ($orderId)")
                }
            }
        }
        else -> {
            val payment = uiState.paymentDetail
            val paymentStatus = uiState.paymentStatus
            val paymentStatusLabel = buildString {
                val status = payment?.status ?: paymentStatus?.status
                if (status != null) {
                    append(
                        "Payment: ${com.emmanuelyator.mydistro.feature.customer.cart.CustomerCheckout.paymentStatusLabel(status)}"
                    )
                }
                val provider = payment?.provider ?: paymentStatus?.provider ?: payment?.method
                provider?.let { append(" · $it") }
                val mode = com.emmanuelyator.mydistro.feature.customer.cart.CustomerCheckout.providerModeLabel(
                    payment?.providerMode ?: paymentStatus?.providerMode
                )
                mode?.let { append(" · $it") }
                (payment?.mpesaReceiptNumber ?: paymentStatus?.mpesaReceiptNumber)
                    ?.let { append(" · Receipt $it") }
                (payment?.paymentReference ?: paymentStatus?.paymentReference)
                    ?.let { append(" · Ref $it") }
                payment?.amount?.let { append(" · KSh ${it.toInt()}") }
                paymentStatus?.failureReason?.let { append(" · $it") }
            }.ifBlank { null }

            CustomerOrderStatusScreen(
                order = uiState.order!!,
                paymentStatusLabel = paymentStatusLabel,
                trackingLabel = uiState.trackingLabel,
                otpMessage = uiState.otpMessage,
                canRequestOtp = uiState.canRequestDeliveryOtp(),
                isRequestingOtp = uiState.isRequestingOtp,
                canCancel = uiState.canCancel(),
                isCancelling = uiState.isCancelling,
                onRequestOtp = viewModel::requestDeliveryOtp,
                onCancel = viewModel::cancelOrder,
                onBack = onBack
            )
        }
    }
}

@Composable
fun CustomerOrderStatusScreen(
    order: Order,
    onBack: () -> Unit,
    paymentStatusLabel: String? = null,
    trackingLabel: String? = null,
    otpMessage: String? = null,
    canRequestOtp: Boolean = false,
    isRequestingOtp: Boolean = false,
    canCancel: Boolean = false,
    isCancelling: Boolean = false,
    onRequestOtp: () -> Unit = {},
    onCancel: () -> Unit = {}
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.screenPadding, vertical = Spacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${order.displayId}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MyDistroTheme.colors.heroSurface
                )
                OrderStatusBadge(status = order.status)
            }

            if (paymentStatusLabel != null) {
                Text(
                    text = paymentStatusLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MyDistroTheme.colors.textSecondary,
                    modifier = Modifier.padding(horizontal = Dimens.screenPadding)
                )
                Spacer(modifier = Modifier.height(Spacing.md))
            }

            if (trackingLabel != null) {
                Text(
                    text = trackingLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MyDistroTheme.colors.info,
                    modifier = Modifier.padding(horizontal = Dimens.screenPadding)
                )
                Spacer(modifier = Modifier.height(Spacing.md))
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.screenPadding)
            ) {
                val currentIndex = OrderStatusTimeline.indexOf(order.status)
                    .takeIf { it >= 0 } ?: OrderStatusTimeline.size

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
                        timestamp = if (isCompleted || isCurrent) {
                            order.date.format(DateTimeFormatter.ofPattern("MMM d, yyyy • h:mm a"))
                        } else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            if (canRequestOtp) {
                Button(
                    onClick = onRequestOtp,
                    enabled = !isRequestingOtp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.screenPadding)
                ) {
                    Text(if (isRequestingOtp) "Sending OTP…" else "Request delivery OTP")
                }
                Spacer(modifier = Modifier.height(Spacing.md))
            }

            if (canCancel) {
                Button(
                    onClick = onCancel,
                    enabled = !isCancelling,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.screenPadding)
                ) {
                    Text(if (isCancelling) "Cancelling…" else "Cancel order")
                }
                Spacer(modifier = Modifier.height(Spacing.md))
            }

            if (otpMessage != null) {
                Text(
                    text = otpMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MyDistroTheme.colors.textSecondary,
                    modifier = Modifier.padding(horizontal = Dimens.screenPadding)
                )
                Spacer(modifier = Modifier.height(Spacing.md))
            }

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
                        text = when (order.status) {
                            OrderStatus.IN_TRANSIT ->
                                "Your order is on the way. Request a delivery OTP when the driver arrives."
                            OrderStatus.PAYMENT_PENDING ->
                                "Awaiting M-Pesa payment confirmation."
                            else ->
                                "Track your order status above. Pull to refresh from the orders list."
                        },
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp)
        ) {
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

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 16.dp)
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
