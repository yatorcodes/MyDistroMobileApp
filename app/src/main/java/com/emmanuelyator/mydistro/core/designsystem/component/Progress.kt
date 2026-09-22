package com.emmanuelyator.mydistro.core.designsystem.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing

/**
 * Trip completion bar with its own caption, e.g. "2/3 stops completed".
 *
 * The bar and caption are collapsed into a single semantics node so a screen
 * reader announces the meaning once instead of reading a bare percentage
 * followed by the text.
 */
@Composable
fun TripProgressBar(
    completed: Int,
    total: Int,
    modifier: Modifier = Modifier,
    trackColor: Color = MyDistroTheme.colors.neutralContainer,
    progressColor: Color = MaterialTheme.colorScheme.secondary,
    captionColor: Color = MyDistroTheme.colors.textSecondary,
    showCaption: Boolean = true
) {
    val fraction = if (total <= 0) 0f else completed.toFloat() / total
    val animatedFraction by animateFloatAsState(
        targetValue = fraction.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 420),
        label = "tripProgress"
    )
    val caption = "$completed/$total stops completed"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clearAndSetSemantics { contentDescription = caption }
    ) {
        LinearProgressIndicator(
            progress = { animatedFraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = progressColor,
            trackColor = trackColor,
            strokeCap = StrokeCap.Round
        )
        if (showCaption) {
            Spacer(Modifier.height(Spacing.sm))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = caption,
                    style = MaterialTheme.typography.bodySmall,
                    color = captionColor
                )
            }
        }
    }
}
