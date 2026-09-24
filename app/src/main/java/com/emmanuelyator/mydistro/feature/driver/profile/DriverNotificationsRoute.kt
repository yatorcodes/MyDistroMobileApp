package com.emmanuelyator.mydistro.feature.driver.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroTopBar
import com.emmanuelyator.mydistro.core.designsystem.theme.Dimens
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing

data class MockNotification(
    val id: String,
    val title: String,
    val body: String,
    val timeAgo: String,
    val isRead: Boolean,
    val icon: ImageVector = Icons.Outlined.LocalShipping
)

object MockNotificationData {
    val notifications = mutableStateListOf(
        MockNotification(
            id = "1",
            title = "New Trip Assigned",
            body = "You have been assigned TRIP-1045 for tomorrow morning. Please review the manifest.",
            timeAgo = "10m ago",
            isRead = false,
            icon = Icons.Outlined.LocalShipping
        ),
        MockNotification(
            id = "2",
            title = "Route Updated",
            body = "The route for TRIP-1042 has been optimized due to traffic. Check your map.",
            timeAgo = "2h ago",
            isRead = false,
            icon = Icons.Outlined.WarningAmber
        ),
        MockNotification(
            id = "3",
            title = "Delivery Confirmed",
            body = "Your delivery at Kibwezi Hardware was successfully marked as completed.",
            timeAgo = "1d ago",
            isRead = true
        ),
        MockNotification(
            id = "4",
            title = "Vehicle Maintenance Reminder",
            body = "Your assigned vehicle KDA 421X is due for scheduled maintenance next week.",
            timeAgo = "3d ago",
            isRead = true
        )
    )
}

@Composable
fun DriverNotificationsRoute(
    onNotificationClick: (String) -> Unit,
    onBack: () -> Unit
) {
    DriverNotificationsScreen(
        notifications = MockNotificationData.notifications,
        onNotificationClick = onNotificationClick,
        onBack = onBack
    )
}

@Composable
fun DriverNotificationsScreen(
    notifications: List<MockNotification>,
    onNotificationClick: (String) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            MyDistroTopBar(
                title = "Notifications",
                onBack = onBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(vertical = Spacing.md)
        ) {
            items(notifications, key = { it.id }) { notification ->
                NotificationItem(
                    notification = notification,
                    onClick = { onNotificationClick(notification.id) }
                )
            }
        }
    }
}

@Composable
private fun NotificationItem(
    notification: MockNotification,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = if (notification.isRead) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.screenPadding, vertical = Spacing.lg),
            verticalAlignment = Alignment.Top
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (notification.isRead) MaterialTheme.colorScheme.surfaceVariant
                        else MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = notification.icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (notification.isRead) MyDistroTheme.colors.textSecondary
                    else MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(Spacing.lg))

            // Text content
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold,
                        color = MyDistroTheme.colors.textPrimary
                    )
                    
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(Spacing.xs))
                
                Text(
                    text = notification.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (notification.isRead) MyDistroTheme.colors.textSecondary else MyDistroTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(Spacing.sm))
                
                Text(
                    text = notification.timeAgo,
                    style = MaterialTheme.typography.labelSmall,
                    color = MyDistroTheme.colors.textTertiary
                )
            }
        }
    }
}

@Composable
fun DriverNotificationDetailsRoute(
    notificationId: String,
    onBack: () -> Unit
) {
    val notification = MockNotificationData.notifications.find { it.id == notificationId }
    
    Scaffold(
        topBar = {
            MyDistroTopBar(
                title = "Message",
                onBack = onBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (notification != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = Dimens.screenPadding, vertical = Spacing.lg)
            ) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MyDistroTheme.colors.textPrimary
                )
                
                Spacer(modifier = Modifier.height(Spacing.sm))
                
                Text(
                    text = notification.timeAgo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MyDistroTheme.colors.textTertiary
                )
                
                Spacer(modifier = Modifier.height(Spacing.xl))
                
                Text(
                    text = notification.body,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MyDistroTheme.colors.textSecondary
                )
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Notification not found")
            }
        }
    }
}
