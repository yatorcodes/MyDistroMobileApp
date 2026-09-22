package com.emmanuelyator.mydistro.feature.driver.trips

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.emmanuelyator.mydistro.core.designsystem.component.EmptyState
import com.emmanuelyator.mydistro.core.designsystem.component.ErrorBanner
import com.emmanuelyator.mydistro.core.designsystem.component.ErrorState
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroCard
import com.emmanuelyator.mydistro.core.designsystem.component.SegmentedTabs
import com.emmanuelyator.mydistro.core.designsystem.component.SkeletonBlock
import com.emmanuelyator.mydistro.core.designsystem.theme.Dimens
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.PillShape
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing
import com.emmanuelyator.mydistro.core.designsystem.theme.StatusBarIcons
import com.emmanuelyator.mydistro.core.model.Driver
import com.emmanuelyator.mydistro.core.model.Trip
import com.emmanuelyator.mydistro.feature.driver.component.TripCard
import com.emmanuelyator.mydistro.feature.driver.data.MockTripData
import java.util.Calendar

@Composable
fun DriverTripsRoute(
    onTripClick: (String) -> Unit,
    viewModel: DriverTripsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DriverTripsContent(
        uiState = uiState,
        onTabSelected = viewModel::onTabSelected,
        onTripClick = onTripClick,
        onRefresh = viewModel::refresh,
        onRefreshErrorDismissed = viewModel::onRefreshErrorDismissed
    )
}

@Composable
private fun DriverTripsContent(
    uiState: DriverTripsUiState,
    onTabSelected: (TripsTab) -> Unit,
    onTripClick: (String) -> Unit,
    onRefresh: () -> Unit,
    onRefreshErrorDismissed: () -> Unit,
    modifier: Modifier = Modifier
) {
    StatusBarIcons(dark = true)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        // Insets are applied explicitly below. The bottom navigation bar lives
        // outside this Scaffold and handles its own navigation-bar inset, so
        // letting Scaffold add one here would leave a gap above it.
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            DriverHeader(
                driver = uiState.driver,
                onNotificationsClick = {},
                onRefreshClick = onRefresh,
                modifier = Modifier.padding(
                    horizontal = Dimens.screenPadding,
                    vertical = Spacing.md
                )
            )

            SegmentedTabs(
                options = listOf("Active Trips", "Trip History"),
                selectedIndex = uiState.selectedTab.ordinal,
                onSelect = { index -> onTabSelected(TripsTab.entries[index]) },
                modifier = Modifier.padding(horizontal = Dimens.screenPadding)
            )

            AnimatedVisibility(visible = uiState.refreshError != null) {
                ErrorBanner(
                    message = uiState.refreshError.orEmpty(),
                    onDismiss = onRefreshErrorDismissed,
                    modifier = Modifier.padding(
                        horizontal = Dimens.screenPadding,
                        vertical = Spacing.md
                    )
                )
            }

            TripsList(
                state = uiState.visibleTrips,
                tab = uiState.selectedTab,
                isRefreshing = uiState.isRefreshing,
                onTripClick = onTripClick,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun TripsList(
    state: UiState<List<Trip>>,
    tab: TripsTab,
    isRefreshing: Boolean,
    onTripClick: (String) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        when (state) {
            UiState.Loading -> TripListSkeleton()

            is UiState.Success -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = Dimens.screenPadding,
                    end = Dimens.screenPadding,
                    top = Spacing.lg,
                    // Clears the bottom navigation bar so the last card is
                    // fully scrollable into view.
                    bottom = Spacing.huge
                ),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                items(
                    items = state.data,
                    // Stable keys keep scroll position and animations correct
                    // when a trip's status changes underneath the list.
                    key = { trip -> trip.id }
                ) { trip ->
                    TripCard(trip = trip, onClick = { onTripClick(trip.id) })
                }
            }

            UiState.Empty -> EmptyState(
                title = when (tab) {
                    TripsTab.ACTIVE -> "No active trips"
                    TripsTab.HISTORY -> "No completed trips yet"
                },
                description = when (tab) {
                    TripsTab.ACTIVE ->
                        "Trips assigned to you by your distributor will appear here."
                    TripsTab.HISTORY ->
                        "Trips you finish will be listed here for your records."
                },
                icon = Icons.Outlined.LocalShipping,
                actionText = "Refresh",
                onAction = onRefresh
            )

            is UiState.Error -> ErrorState(error = state.error, onRetry = onRefresh)
        }

        // Refresh indicator overlays the content rather than displacing it.
        // Pull-to-refresh is a later addition; an explicit control is also the
        // accessible option for switch-access and TalkBack users.
        if (isRefreshing) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = Spacing.md)
                    .clip(PillShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(Spacing.sm)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(Dimens.iconMd),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun TripListSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.screenPadding, vertical = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        repeat(3) {
            MyDistroCard {
                SkeletonBlock(modifier = Modifier.fillMaxWidth(0.45f), height = 18.dp)
                Spacer(Modifier.height(Spacing.md))
                SkeletonBlock(modifier = Modifier.fillMaxWidth(0.8f), height = 14.dp)
                Spacer(Modifier.height(Spacing.sm))
                SkeletonBlock(modifier = Modifier.fillMaxWidth(0.6f), height = 14.dp)
                Spacer(Modifier.height(Spacing.lg))
                SkeletonBlock(modifier = Modifier.fillMaxWidth(), height = 6.dp)
            }
        }
    }
}

@Composable
private fun DriverHeader(
    driver: Driver?,
    onNotificationsClick: () -> Unit,
    onRefreshClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DriverAvatar(initials = driver?.initials() ?: "–")

        Spacer(Modifier.width(Spacing.md))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = greetingForNow(),
                style = MaterialTheme.typography.bodySmall,
                color = MyDistroTheme.colors.textSecondary
            )
            Text(
                text = driver?.firstName ?: "Driver",
                style = MaterialTheme.typography.titleLarge,
                color = MyDistroTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (driver != null) {
                Text(
                    text = driver.vehicleRegistration,
                    style = MaterialTheme.typography.bodySmall,
                    color = MyDistroTheme.colors.textTertiary
                )
            }
        }

        // Pull-to-refresh alone is not reachable by TalkBack or switch access,
        // so the gesture is backed by a real control.
        IconButton(onClick = onRefreshClick) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = "Refresh trips",
                tint = MyDistroTheme.colors.textPrimary
            )
        }

        IconButton(onClick = onNotificationsClick) {
            Icon(
                imageVector = Icons.Outlined.NotificationsNone,
                contentDescription = "Notifications",
                tint = MyDistroTheme.colors.textPrimary
            )
        }
    }
}

@Composable
private fun DriverAvatar(initials: String) {
    Box(
        modifier = Modifier
            .size(Dimens.avatarMd)
            .clip(PillShape)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

private fun Driver.initials(): String =
    fullName.split(' ')
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }

/**
 * Time-of-day greeting. Uses [Calendar] rather than java.time because minSdk is
 * 24 and the project has no core library desugaring configured.
 */
private fun greetingForNow(): String =
    when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> "Good morning,"
        in 12..16 -> "Good afternoon,"
        else -> "Good evening,"
    }

@Preview(showBackground = true, backgroundColor = 0xFFF7F8FA)
@Composable
private fun DriverTripsLoadingPreview() {
    MyDistroTheme {
        DriverTripsContent(
            uiState = DriverTripsUiState(),
            onTabSelected = {},
            onTripClick = {},
            onRefresh = {},
            onRefreshErrorDismissed = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F8FA, heightDp = 900)
@Composable
private fun DriverTripsSuccessPreview() {
    MyDistroTheme {
        DriverTripsContent(
            uiState = DriverTripsUiState(
                driver = Driver(
                    id = "drv-001",
                    fullName = "Alex Mwangi",
                    phoneNumber = "+254712345678",
                    vehicleRegistration = "KDA 421X"
                ),
                activeTrips = UiState.Success(
                    listOf(
                        MockTripData.deliveryTripInProgress,
                        MockTripData.restockTripScheduled
                    )
                )
            ),
            onTabSelected = {},
            onTripClick = {},
            onRefresh = {},
            onRefreshErrorDismissed = {}
        )
    }
}
