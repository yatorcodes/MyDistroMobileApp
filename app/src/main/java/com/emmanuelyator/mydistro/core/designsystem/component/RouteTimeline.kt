package com.emmanuelyator.mydistro.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.emmanuelyator.mydistro.core.designsystem.theme.Dimens
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.PillShape
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing
import com.emmanuelyator.mydistro.core.model.StopStatus

/**
 * A single row in the route timeline: numbered node, vertical connector, and the
 * stop's details. Completed nodes show a tick rather than their number, which
 * makes progress readable without reading any text.
 *
 * Generic over the domain: callers pass presentation values, so this is reusable
 * for the driver's route and the customer's order lifecycle.
 */
@Composable
fun TimelineRow(
    title: String,
    status: StopStatus,
    modifier: Modifier = Modifier,
    sequenceLabel: String? = null,
    subtitle: String? = null,
    metaText: String? = null,
    isLast: Boolean = false,
    trailingContent: @Composable (() -> Unit)? = null
) {
    val nodeColor = status.nodeColor()

    // IntrinsicSize.Min gives the row a concrete height driven by the text
    // column, which is what lets the connector line stretch with `weight`.
    Row(modifier = modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        TimelineGutter(
            nodeColor = nodeColor,
            sequenceLabel = sequenceLabel,
            isComplete = status == StopStatus.COMPLETED,
            isLast = isLast
        )

        Spacer(Modifier.width(Spacing.md))

        Column(
            modifier = Modifier
                .weight(1f)
                // Bottom padding is what creates the connector's length; without
                // it consecutive rows would sit flush and the line would vanish.
                .padding(bottom = if (isLast) 0.dp else Spacing.xl)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MyDistroTheme.colors.textPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (subtitle != null) {
                        Spacer(Modifier.height(Spacing.xxs))
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MyDistroTheme.colors.textSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                if (trailingContent != null) {
                    Spacer(Modifier.width(Spacing.sm))
                    trailingContent()
                }
            }
            if (metaText != null) {
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = metaText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MyDistroTheme.colors.textTertiary
                )
            }
        }
    }
}

@Composable
private fun TimelineGutter(
    nodeColor: Color,
    sequenceLabel: String?,
    isComplete: Boolean,
    isLast: Boolean
) {
    Column(
        modifier = Modifier
            .width(Dimens.timelineNode)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.timelineNode)
                .clip(PillShape)
                .background(nodeColor),
            contentAlignment = Alignment.Center
        ) {
            if (isComplete) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(Dimens.iconSm)
                )
            } else if (sequenceLabel != null) {
                Text(
                    text = sequenceLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White
                )
            }
        }
        if (!isLast) {
            Box(
                modifier = Modifier
                    .width(Dimens.timelineConnectorWidth)
                    .weight(1f)
                    .background(MyDistroTheme.colors.border)
            )
        }
    }
}

@Composable
private fun StopStatus.nodeColor(): Color = when (this) {
    StopStatus.COMPLETED -> MyDistroTheme.colors.success
    StopStatus.IN_PROGRESS -> MyDistroTheme.colors.info
    StopStatus.PENDING -> MyDistroTheme.colors.borderStrong
    StopStatus.FAILED -> MyDistroTheme.colors.danger
    StopStatus.SKIPPED -> MyDistroTheme.colors.neutral
}
