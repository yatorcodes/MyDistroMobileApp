package com.emmanuelyator.mydistro.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.emmanuelyator.mydistro.core.designsystem.theme.Dimens
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing

/**
 * Primary call to action — orange, full width. There should be at most one of
 * these visible at a time, which is what keeps the accent colour meaningful.
 *
 * While [loading] is true the button is disabled and keeps its footprint, so the
 * layout does not jump. That also makes it the app's duplicate-submission guard:
 * callers set loading from their UI state instead of tracking a separate flag.
 */
@Composable
fun MyDistroPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    // A restrained 2% dip: enough to feel responsive, not enough to feel toy-like.
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 90),
        label = "primaryButtonScale"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = Dimens.buttonHeight)
            .scale(scale),
        enabled = enabled && !loading,
        shape = MaterialTheme.shapes.small,
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
            disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.45f),
            disabledContentColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.8f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        ButtonContent(text = text, loading = loading, leadingIcon = leadingIcon)
    }
}

/** Navy secondary action, e.g. "View Map" under the trip timeline. */
@Composable
fun MyDistroSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = Dimens.buttonHeight),
        enabled = enabled && !loading,
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        ButtonContent(text = text, loading = loading, leadingIcon = leadingIcon)
    }
}

/** Low-emphasis bordered action, used for the "Login" pair on the signup screen. */
@Composable
fun MyDistroOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = Dimens.buttonHeight),
        enabled = enabled,
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(Dimens.hairline, MyDistroTheme.colors.border),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MyDistroTheme.colors.textPrimary
        )
    ) {
        ButtonContent(text = text, loading = false, leadingIcon = leadingIcon)
    }
}

/** Text-only action such as "Forgot password?". */
@Composable
fun MyDistroTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    accent: Boolean = false
) {
    val contentColor by animateColorAsState(
        targetValue = if (accent) {
            MaterialTheme.colorScheme.secondary
        } else {
            MyDistroTheme.colors.textSecondary
        },
        label = "textButtonColor"
    )
    TextButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = Dimens.minTouchTarget),
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(contentColor = contentColor)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun ButtonContent(
    text: String,
    loading: Boolean,
    leadingIcon: ImageVector?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(Dimens.iconMd),
                strokeWidth = 2.dp,
                color = LocalContentColor.current
            )
        } else if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(Dimens.iconMd)
            )
        }
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}
