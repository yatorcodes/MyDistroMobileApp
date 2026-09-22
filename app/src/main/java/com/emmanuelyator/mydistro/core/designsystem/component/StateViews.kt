package com.emmanuelyator.mydistro.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.emmanuelyator.mydistro.core.common.AppError
import com.emmanuelyator.mydistro.core.designsystem.theme.Dimens
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.PillShape
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing

@Composable
fun LoadingState(
    modifier: Modifier = Modifier,
    message: String? = null
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.secondary,
            strokeWidth = 3.dp
        )
        if (message != null) {
            Spacer(Modifier.height(Spacing.lg))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MyDistroTheme.colors.textSecondary
            )
        }
    }
}

/**
 * Placeholder blocks shown while the trip list loads. Skeletons rather than a
 * bare spinner, because the list layout is known ahead of time and this makes
 * the load feel shorter.
 */
@Composable
fun SkeletonBlock(
    modifier: Modifier = Modifier,
    height: Dp = 20.dp
) {
    Box(
        modifier = modifier
            .height(height)
            .clip(MaterialTheme.shapes.extraSmall)
            .background(MyDistroTheme.colors.neutralContainer)
    )
}

@Composable
fun EmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Outlined.Inbox,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    StateMessage(
        icon = icon,
        iconTint = MyDistroTheme.colors.textSecondary,
        iconBackground = MyDistroTheme.colors.neutralContainer,
        title = title,
        description = description,
        actionText = actionText,
        onAction = onAction,
        modifier = modifier
    )
}

/**
 * Error state driven by [AppError], so the wording stays consistent and the
 * retry affordance is never accidentally omitted.
 */
@Composable
fun ErrorState(
    error: AppError,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null
) {
    StateMessage(
        icon = Icons.Outlined.CloudOff,
        iconTint = MyDistroTheme.colors.danger,
        iconBackground = MyDistroTheme.colors.dangerContainer,
        title = "Couldn't load this",
        description = error.message,
        actionText = onRetry?.let { "Try again" },
        actionIcon = Icons.Outlined.Refresh,
        onAction = onRetry,
        modifier = modifier
    )
}

@Composable
private fun StateMessage(
    icon: ImageVector,
    iconTint: Color,
    iconBackground: Color,
    title: String,
    description: String,
    actionText: String?,
    onAction: (() -> Unit)?,
    modifier: Modifier = Modifier,
    actionIcon: ImageVector? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.screenPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(PillShape)
                .background(iconBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(Modifier.height(Spacing.xl))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MyDistroTheme.colors.textPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MyDistroTheme.colors.textSecondary,
            textAlign = TextAlign.Center
        )
        if (actionText != null && onAction != null) {
            Spacer(Modifier.height(Spacing.xl))
            MyDistroOutlinedButton(
                text = actionText,
                onClick = onAction,
                leadingIcon = actionIcon,
                modifier = Modifier.fillMaxWidth(0.62f)
            )
        }
    }
}

/**
 * Inline banner for non-blocking failures — for example a refresh that failed
 * while cached content is still on screen.
 */
@Composable
fun ErrorBanner(
    message: String,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null
) {
    MyDistroCard(
        modifier = modifier.fillMaxWidth(),
        containerColor = MyDistroTheme.colors.dangerContainer,
        borderColor = MyDistroTheme.colors.danger.copy(alpha = 0.25f),
        contentPadding = Spacing.md
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MyDistroTheme.colors.danger
        )
        if (onDismiss != null) {
            MyDistroTextButton(text = "Dismiss", onClick = onDismiss)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F8FA)
@Composable
private fun EmptyStatePreview() {
    MyDistroTheme {
        EmptyState(
            title = "No active trips",
            description = "New trips assigned to you by your distributor will show up here.",
            actionText = "Refresh",
            onAction = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F8FA)
@Composable
private fun ErrorStatePreview() {
    MyDistroTheme {
        ErrorState(error = AppError.NoConnection, onRetry = {})
    }
}
