package com.emmanuelyator.mydistro.feature.driver.tripdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emmanuelyator.mydistro.core.common.UiState
import com.emmanuelyator.mydistro.core.designsystem.component.ErrorState
import com.emmanuelyator.mydistro.core.designsystem.component.LoadingState
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroCard
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroPrimaryButton
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroSecondaryButton
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroTopBar
import com.emmanuelyator.mydistro.core.designsystem.component.StopStatusBadge
import com.emmanuelyator.mydistro.core.designsystem.component.TimelineRow
import com.emmanuelyator.mydistro.core.designsystem.component.TripProgressBar
import com.emmanuelyator.mydistro.core.designsystem.component.TripStatusBadge
import com.emmanuelyator.mydistro.core.designsystem.component.TripTypeTag
import com.emmanuelyator.mydistro.core.designsystem.theme.Dimens
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing
import com.emmanuelyator.mydistro.core.designsystem.theme.StatusBarIcons
import com.emmanuelyator.mydistro.core.model.StopStatus
import com.emmanuelyator.mydistro.core.model.Trip
import com.emmanuelyator.mydistro.core.model.TripItem
import com.emmanuelyator.mydistro.core.model.TripStop
import com.emmanuelyator.mydistro.core.model.TripType
import com.emmanuelyator.mydistro.feature.driver.data.MockTripData

@Composable
fun TripDetailsRoute(
    onBack: () -> Unit,
    onConfirmDelivery: (tripId: String, stopId: String) -> Unit,
    onViewMap: () -> Unit,
    viewModel: TripDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TripDetailsContent(
        state = uiState,
        onBack = onBack,
        onConfirmDelivery = onConfirmDelivery,
        onViewMap = onViewMap
    )
}

@Composable
private fun TripDetailsContent(
    state: UiState<Trip>,
    onBack: () -> Unit,
    onConfirmDelivery: (tripId: String, stopId: String) -> Unit,
    onViewMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    StatusBarIcons(dark = true)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { MyDistroTopBar(title = "Trip Details", onBack = onBack) }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (state) {
                UiState.Loading -> LoadingState(message = "Loading trip…")
                UiState.Empty -> LoadingState()
                is UiState.Error -> ErrorState(error = state.error)
                is UiState.Success -> TripDetailsBody(
                    trip = state.data,
                    onConfirmDelivery = onConfirmDelivery,
                    onViewMap = onViewMap
                )
            }
        }
    }
}

@Composable
private fun TripDetailsBody(
    trip: Trip,
    onConfirmDelivery: (tripId: String, stopId: String) -> Unit,
    onViewMap: () -> Unit
) {
    // The next stop the driver can confirm. Null for restock trips and for
    // finished trips, which is why the CTA is conditional rather than assumed.
    val confirmableStop = trip.activeStop?.takeIf { it.requiresDeliveryConfirmation }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            // No navigationBarsPadding here: the Scaffold's innerPadding
            // already accounts for the system navigation bar.
            .padding(horizontal = Dimens.screenPadding)
            .padding(bottom = Spacing.xxl)
    ) {
        TripSummaryCard(trip = trip)

        Spacer(Modifier.height(Spacing.xl))

        SectionHeading(text = "Route")
        Spacer(Modifier.height(Spacing.md))
        RouteCard(trip = trip)

        Spacer(Modifier.height(Spacing.xl))

        SectionHeading(
            text = "Items on board",
            trailingText = "${trip.itemsOnBoard.size} line items"
        )
        Spacer(Modifier.height(Spacing.md))
        ItemsCard(items = trip.itemsOnBoard)

        Spacer(Modifier.height(Spacing.xl))

        if (confirmableStop != null) {
            MyDistroPrimaryButton(
                text = "Confirm delivery · ${confirmableStop.location.name}",
                onClick = { onConfirmDelivery(trip.id, confirmableStop.id) }
            )
            Spacer(Modifier.height(Spacing.md))
        }

        MyDistroSecondaryButton(
            text = "View Map",
            onClick = onViewMap,
            leadingIcon = Icons.Outlined.Map
        )
    }
}

/** Trip number, type, status and the origin/progress overview. */
@Composable
private fun TripSummaryCard(trip: Trip) {
    MyDistroCard(containerColor = MaterialTheme.colorScheme.surface) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TripTypeTag(type = trip.type)
            Spacer(Modifier.width(Spacing.sm))
            Text(
                text = trip.tripNumber,
                style = MaterialTheme.typography.titleMedium,
                color = MyDistroTheme.colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
            TripStatusBadge(status = trip.status)
        }

        Spacer(Modifier.height(Spacing.lg))

        DetailRow(label = "Pickup", value = trip.origin.name)
        DetailRow(label = "Pickup address", value = trip.origin.address)
        DetailRow(label = "Destination", value = trip.finalDestinationLabel())
        DetailRow(label = "Scheduled", value = trip.scheduledStartLabel)
        DetailRow(label = "Distance", value = "${trip.totalDistanceKm} km")

        Spacer(Modifier.height(Spacing.lg))

        TripProgressBar(
            completed = trip.completedStopCount,
            total = trip.totalStopCount
        )
    }
}

@Composable
private fun RouteCard(trip: Trip) {
    MyDistroCard {
        // The origin is rendered as a completed node: the driver has already
        // loaded, so it is part of the route, not a pending stop.
        TimelineRow(
            title = trip.origin.name,
            subtitle = trip.origin.address,
            status = StopStatus.COMPLETED,
            metaText = "Departed ${trip.scheduledStartLabel}",
            isLast = trip.stops.isEmpty()
        )

        trip.stops.sortedBy { it.sequence }.forEachIndexed { index, stop ->
            TimelineRow(
                title = stop.location.name,
                subtitle = stop.location.address,
                status = stop.status,
                sequenceLabel = stop.sequence.toString(),
                metaText = stop.metaLabel(trip.type),
                isLast = index == trip.stops.lastIndex,
                trailingContent = { StopStatusBadge(status = stop.status) }
            )
        }
    }
}

@Composable
private fun ItemsCard(items: List<TripItem>) {
    MyDistroCard {
        if (items.isEmpty()) {
            Text(
                text = "No items recorded for this trip.",
                style = MaterialTheme.typography.bodyMedium,
                color = MyDistroTheme.colors.textSecondary
            )
            return@MyDistroCard
        }

        items.forEachIndexed { index, item ->
            ItemRow(item = item)
            if (index != items.lastIndex) {
                Spacer(Modifier.height(Spacing.md))
            }
        }
    }
}

@Composable
private fun ItemRow(item: TripItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .background(MyDistroTheme.colors.neutralContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Inventory2,
                contentDescription = null,
                tint = MyDistroTheme.colors.textSecondary,
                modifier = Modifier.size(Dimens.iconMd)
            )
        }
        Spacer(Modifier.width(Spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.productName,
                style = MaterialTheme.typography.titleSmall,
                color = MyDistroTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = item.unitLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MyDistroTheme.colors.textSecondary
            )
        }
        Text(
            text = "× ${item.quantity}",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun SectionHeading(text: String, trailingText: String? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = MyDistroTheme.colors.textPrimary
        )
        if (trailingText != null) {
            Text(
                text = trailingText,
                style = MaterialTheme.typography.bodySmall,
                color = MyDistroTheme.colors.textSecondary
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MyDistroTheme.colors.textSecondary,
            modifier = Modifier.width(110.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MyDistroTheme.colors.textPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Restock trips end at a warehouse, delivery trips at their last customer, so
 * "destination" cannot be derived the same way for both.
 */
private fun Trip.finalDestinationLabel(): String =
    stops.maxByOrNull { it.sequence }?.location?.name ?: "No destination assigned"

/** Order number for customer stops; the ETA alone for warehouse legs. */
private fun TripStop.metaLabel(tripType: TripType): String {
    val eta = "ETA $etaLabel · $distanceFromPreviousKm km"
    return when {
        orderNumber != null -> "$orderNumber · $eta"
        tripType == TripType.RESTOCK -> "Warehouse intake · $eta"
        else -> eta
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F8FA, heightDp = 1400)
@Composable
private fun TripDetailsDeliveryPreview() {
    MyDistroTheme {
        TripDetailsContent(
            state = UiState.Success(MockTripData.deliveryTripInProgress),
            onBack = {},
            onConfirmDelivery = { _, _ -> },
            onViewMap = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F8FA, heightDp = 1100)
@Composable
private fun TripDetailsRestockPreview() {
    MyDistroTheme {
        TripDetailsContent(
            state = UiState.Success(MockTripData.restockTripScheduled),
            onBack = {},
            onConfirmDelivery = { _, _ -> },
            onViewMap = {}
        )
    }
}
