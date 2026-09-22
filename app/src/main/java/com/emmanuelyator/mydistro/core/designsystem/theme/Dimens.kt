package com.emmanuelyator.mydistro.core.designsystem.theme

import androidx.compose.ui.unit.dp

/**
 * 8dp-based spacing scale. Every gap in the app should come from here so that
 * rhythm stays consistent and a density change is a one-line edit.
 */
object Spacing {
    val xxs = 2.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val xxxl = 32.dp
    val huge = 48.dp
}

object Dimens {
    /** Android's minimum accessible touch target. */
    val minTouchTarget = 48.dp
    val buttonHeight = 54.dp
    val textFieldHeight = 56.dp
    val iconButton = 44.dp
    val iconSm = 16.dp
    val iconMd = 20.dp
    val iconLg = 24.dp
    val hairline = 1.dp

    /** Diameter of the numbered nodes in the route timeline. */
    val timelineNode = 26.dp
    val timelineConnectorWidth = 2.dp

    val avatarSm = 36.dp
    val avatarMd = 44.dp
    val avatarLg = 72.dp

    val screenPadding = 20.dp
    val cardPadding = 16.dp

    /** Caps content width so the layout stays readable on tablets and foldables. */
    val maxContentWidth = 560.dp
}
