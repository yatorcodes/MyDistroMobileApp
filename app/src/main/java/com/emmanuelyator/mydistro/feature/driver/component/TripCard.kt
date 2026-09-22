package com.emmanuelyator.mydistro.feature.driver.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroCard
import com.emmanuelyator.mydistro.core.designsystem.component.TripProgressBar
import com.emmanuelyator.mydistro.core.designsystem.component.TripStatusBadge
import com.emmanuelyator.mydistro.core.designsystem.component.TripTypeTag
import com.emmanuelyator.mydistro.core.designsystem.component.label
import com.emmanuelyator.mydistro.core.designsystem.theme.Dimens
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.PillShape
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing
import com.emmanuelyator.mydistro.core.model.Trip
import com.emmanuelyator.mydistro.core.model.TripStatus
import com.emmanuelyator.mydistro.core.model.TripType
import com.emmanuelyator.mydistro.feature.driver.data.MockTripData

/**
 * One trip in the driver's list, tappable as a single large target.
 *
 * The trip currently under way is rendered on navy to pull the eye to the one
 * thing the driver has to act on; everything else stays on white. DELIVERY and
 * RESTOCK are separated by the coloured type tag and by the route summary
 * wording, so the two kinds never blur together in a scrolling list.
 */
@Composable
fun TripCard(
    trip: Trip,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val highlighted = trip.status == TripStatus.ONGOING

    val titleColor = if (highlighted) {
        MyDistroTheme.colors.onHeroSurface
    } else {
        MyDistroTheme.colors.textPrimary
    }
    val bodyColor = if (highlighted) {
        MyDistroTheme.colors.onHeroSurfaceVariant
    } else {
        MyDistroTheme.colors.textSecondary
    }

    MyDistroCard(
        modifier = modifier.semantics {
            contentDescription = trip.accessibilityLabel()
        },
        onClick = onClick,
        containerColor = if (highlighted) {
            MyDistroTheme.colors.heroSurface
        } else {
            MaterialTheme.colorScheme.surface
        },
        borderColor = if (highlighted) {
            MyDistroTheme.colors.heroSurface
        } else {
            MyDistroTheme.colors.border
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TripTypeTag(type = trip.type)
            Spacer(Modifier.width(Spacing.sm))
            Text(
                text = trip.tripNumber,
                style = MaterialTheme.typography.titleSmall,
                color = titleColor,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            TripStatusBadge(status = trip.status)
        }

        Spacer(Modifier.height(Spacing.lg))

        RouteSummary(trip = trip, titleColor = titleColor, bodyColor = bodyColor)

        // A progress bar on a not-yet-started trip reads as a stalled trip, so
        // it only appears once there is progress to show.
        if (trip.totalStopCount > 0 && trip.status != TripStatus.SCHEDULED) {
            Spacer(Modifier.height(Spacing.lg))
            TripProgressBar(
                completed = trip.completedStopCount,
                total = trip.totalStopCount,
                trackColor = if (highlighted) {
                    Color.White.copy(alpha = 0.18f)
                } else {
                    MyDistroTheme.colors.neutralContainer
                },
                captionColor = bodyColor
            )
        }
    }
}

/**
 * Origin, leg summary and destinations. A restock trip has a single warehouse
 * destination, so the copy adapts rather than saying "1 stops".
 */
@Composable
private fun RouteSummary(
    trip: Trip,
    titleColor: Color,
    bodyColor: Color
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = Icons.Filled.Circle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .padding(top = 5.dp)
                .size(9.dp)
        )
        Spacer(Modifier.width(Spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = trip.origin.name,
                style = MaterialTheme.typography.titleSmall,
                color = titleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(Spacing.sm))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Route,
                    contentDescription = null,
                    tint = bodyColor,
                    modifier = Modifier.size(Dimens.iconSm)
                )
                Text(
                    text = trip.legSummary(),
                    style = MaterialTheme.typography.bodySmall,
                    color = bodyColor
                )
            }

            Spacer(Modifier.height(Spacing.sm))

            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Outlined.Place,
                    contentDescription = null,
                    tint = bodyColor,
                    modifier = Modifier.size(Dimens.iconSm)
                )
                Spacer(Modifier.width(Spacing.xs))
                Text(
                    text = trip.destinationSummary(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = titleColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/** "3 stops · 320 km" for deliveries, "Warehouse restock · 80 km" otherwise. */
private fun Trip.legSummary(): String = when (type) {
    TripType.DELIVERY -> {
        val stopLabel = if (totalStopCount == 1) "1 stop" else "$totalStopCount stops"
        "$stopLabel · $totalDistanceKm km"
    }
    TripType.RESTOCK -> "Warehouse restock · $totalDistanceKm km"
}

private fun Trip.destinationSummary(): String =
    stops.sortedBy { it.sequence }
        .joinToString(separator = ", ") { it.location.name }
        .ifEmpty { "No stops assigned yet" }

/**
 * Collapses the card into one spoken sentence, so a screen reader announces the
 * trip as a unit instead of reading six disconnected fragments.
 */
private fun Trip.accessibilityLabel(): String = buildString {
    append("${type.label.lowercase()} trip $tripNumber, ${status.label}. ")
    append("From ${origin.name}. ")
    append("${totalStopCount} stops, $totalDistanceKm kilometres. ")
    append("$completedStopCount of $totalStopCount stops completed.")
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F8FA)
@Composable
private fun TripCardOngoingPreview() {
    MyDistroTheme {
        Box(Modifier.padding(Spacing.lg)) {
            TripCard(trip = MockTripData.deliveryTripInProgress, onClick = {})
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F8FA)
@Composable
private fun TripCardRestockPreview() {
    MyDistroTheme {
        Box(Modifier.padding(Spacing.lg)) {
            TripCard(trip = MockTripData.restockTripScheduled, onClick = {})
        }
    }
}
