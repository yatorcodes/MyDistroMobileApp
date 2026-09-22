package com.emmanuelyator.mydistro.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.PillShape
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing
import com.emmanuelyator.mydistro.core.model.OrderStatus
import com.emmanuelyator.mydistro.core.model.StopStatus
import com.emmanuelyator.mydistro.core.model.TripStatus
import com.emmanuelyator.mydistro.core.model.TripType

/**
 * Semantic meaning of a badge, kept separate from the specific enum being shown.
 * Mapping each domain enum to a tone in one place is what keeps "Completed"
 * green and "Pending" amber everywhere in the app.
 */
enum class BadgeTone { Success, Info, Warning, Danger, Neutral, Accent }

/**
 * Small pill label. Colour alone never carries the meaning — the text does — so
 * these remain readable for colour-blind users and in screen readers.
 */
@Composable
fun StatusBadge(
    text: String,
    tone: BadgeTone,
    modifier: Modifier = Modifier,
    showDot: Boolean = false
) {
    val content = tone.contentColor()
    val container = tone.containerColor()

    Row(
        modifier = modifier
            .clip(PillShape)
            .background(container)
            .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        if (showDot) {
            Row(
                modifier = Modifier
                    .size(Spacing.sm)
                    .clip(PillShape)
                    .background(content)
            ) {}
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = content,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Square-ish tag for the trip type. DELIVERY and RESTOCK get different colours
 * *and* different words so the two trip kinds are distinguishable at a glance in
 * a scrolling list.
 */
@Composable
fun TripTypeTag(
    type: TripType,
    modifier: Modifier = Modifier
) {
    val (container, content) = when (type) {
        TripType.DELIVERY -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
        TripType.RESTOCK -> MyDistroTheme.colors.info to Color.White
    }
    Text(
        text = type.label,
        style = MaterialTheme.typography.labelSmall,
        color = content,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(container)
            .padding(horizontal = Spacing.sm, vertical = 3.dp)
    )
}

@Composable
fun TripStatusBadge(status: TripStatus, modifier: Modifier = Modifier) {
    StatusBadge(
        text = status.label,
        tone = status.tone,
        modifier = modifier,
        showDot = status == TripStatus.ONGOING
    )
}

@Composable
fun StopStatusBadge(status: StopStatus, modifier: Modifier = Modifier) {
    StatusBadge(text = status.label, tone = status.tone, modifier = modifier)
}

@Composable
fun OrderStatusBadge(status: OrderStatus, modifier: Modifier = Modifier) {
    StatusBadge(text = status.label, tone = status.tone, modifier = modifier)
}

// --- Domain enum -> presentation mapping -------------------------------------
// These live in the design system, not in the domain models, so that core/model
// stays free of UI concerns and can be shared with non-Compose code later.

val TripType.label: String
    get() = when (this) {
        TripType.DELIVERY -> "DELIVERY"
        TripType.RESTOCK -> "RESTOCK"
    }

val TripStatus.label: String
    get() = when (this) {
        TripStatus.SCHEDULED -> "Scheduled"
        TripStatus.ONGOING -> "On going"
        TripStatus.COMPLETED -> "Completed"
        TripStatus.DELAYED -> "Delayed"
        TripStatus.CANCELLED -> "Cancelled"
    }

val TripStatus.tone: BadgeTone
    get() = when (this) {
        TripStatus.SCHEDULED -> BadgeTone.Warning
        TripStatus.ONGOING -> BadgeTone.Success
        TripStatus.COMPLETED -> BadgeTone.Neutral
        TripStatus.DELAYED -> BadgeTone.Warning
        TripStatus.CANCELLED -> BadgeTone.Danger
    }

val StopStatus.label: String
    get() = when (this) {
        StopStatus.PENDING -> "Pending"
        StopStatus.IN_PROGRESS -> "In Progress"
        StopStatus.COMPLETED -> "Completed"
        StopStatus.FAILED -> "Failed"
        StopStatus.SKIPPED -> "Skipped"
    }

val StopStatus.tone: BadgeTone
    get() = when (this) {
        StopStatus.PENDING -> BadgeTone.Warning
        StopStatus.IN_PROGRESS -> BadgeTone.Info
        StopStatus.COMPLETED -> BadgeTone.Success
        StopStatus.FAILED -> BadgeTone.Danger
        StopStatus.SKIPPED -> BadgeTone.Neutral
    }

val OrderStatus.label: String
    get() = when (this) {
        OrderStatus.SUBMITTED -> "Submitted"
        OrderStatus.CONFIRMED -> "Confirmed"
        OrderStatus.PAYMENT_PENDING -> "Payment Pending"
        OrderStatus.PAID -> "Paid"
        OrderStatus.ASSIGNED -> "Assigned"
        OrderStatus.DISPATCHED -> "Dispatched"
        OrderStatus.IN_TRANSIT -> "In Transit"
        OrderStatus.DELIVERED -> "Delivered"
        OrderStatus.CLOSED -> "Closed"
        OrderStatus.DELAYED -> "Delayed"
        OrderStatus.CANCELLED -> "Cancelled"
        OrderStatus.PAYMENT_FAILED -> "Payment Failed"
        OrderStatus.DELIVERY_FAILED -> "Delivery Failed"
        OrderStatus.DISPUTED -> "Disputed"
    }

val OrderStatus.tone: BadgeTone
    get() = when (this) {
        OrderStatus.SUBMITTED -> BadgeTone.Neutral
        OrderStatus.CONFIRMED -> BadgeTone.Success
        OrderStatus.PAYMENT_PENDING -> BadgeTone.Warning
        OrderStatus.PAID -> BadgeTone.Success
        OrderStatus.ASSIGNED -> BadgeTone.Info
        OrderStatus.DISPATCHED -> BadgeTone.Info
        OrderStatus.IN_TRANSIT -> BadgeTone.Info
        OrderStatus.DELIVERED -> BadgeTone.Success
        OrderStatus.CLOSED -> BadgeTone.Neutral
        OrderStatus.DELAYED -> BadgeTone.Warning
        OrderStatus.CANCELLED -> BadgeTone.Danger
        OrderStatus.PAYMENT_FAILED -> BadgeTone.Danger
        OrderStatus.DELIVERY_FAILED -> BadgeTone.Danger
        OrderStatus.DISPUTED -> BadgeTone.Danger
    }

@Composable
private fun BadgeTone.contentColor(): Color = when (this) {
    BadgeTone.Success -> MyDistroTheme.colors.success
    BadgeTone.Info -> MyDistroTheme.colors.info
    BadgeTone.Warning -> MyDistroTheme.colors.warning
    BadgeTone.Danger -> MyDistroTheme.colors.danger
    BadgeTone.Neutral -> MyDistroTheme.colors.neutral
    BadgeTone.Accent -> MaterialTheme.colorScheme.secondary
}

@Composable
private fun BadgeTone.containerColor(): Color = when (this) {
    BadgeTone.Success -> MyDistroTheme.colors.successContainer
    BadgeTone.Info -> MyDistroTheme.colors.infoContainer
    BadgeTone.Warning -> MyDistroTheme.colors.warningContainer
    BadgeTone.Danger -> MyDistroTheme.colors.dangerContainer
    BadgeTone.Neutral -> MyDistroTheme.colors.neutralContainer
    BadgeTone.Accent -> MaterialTheme.colorScheme.secondaryContainer
}
