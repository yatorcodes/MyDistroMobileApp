package com.emmanuelyator.mydistro.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.emmanuelyator.mydistro.core.designsystem.theme.Dimens
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme

/**
 * The app's one card container: white surface, hairline border, rounded corners,
 * no shadow. Depth comes from the border and the grey page background rather
 * than elevation, which is what gives the UI its flat, premium look.
 *
 * Pass [onClick] to make it tappable — that routes through Material's clickable
 * card overload so ripple, focus and the "button" accessibility role come for
 * free, and the whole card becomes one large touch target.
 */
@Composable
fun MyDistroCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MyDistroTheme.colors.border,
    shape: Shape = MaterialTheme.shapes.medium,
    contentPadding: Dp = Dimens.cardPadding,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = CardDefaults.cardColors(containerColor = containerColor)
    val border = BorderStroke(Dimens.hairline, borderColor)
    val elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = shape,
            colors = colors,
            border = border,
            elevation = elevation
        ) {
            Column(
                modifier = Modifier.padding(contentPadding),
                content = content
            )
        }
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = shape,
            colors = colors,
            border = border,
            elevation = elevation
        ) {
            Column(
                modifier = Modifier.padding(contentPadding),
                content = content
            )
        }
    }
}

/**
 * Navy variant used for the highlighted active trip and detail headers. Callers
 * are responsible for using [MyDistroTheme.colors.onHeroSurface] on text inside.
 */
@Composable
fun MyDistroHeroCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contentPadding: Dp = Dimens.cardPadding,
    content: @Composable ColumnScope.() -> Unit
) {
    MyDistroCard(
        modifier = modifier,
        onClick = onClick,
        containerColor = MyDistroTheme.colors.heroSurface,
        borderColor = MyDistroTheme.colors.heroSurface,
        contentPadding = contentPadding,
        content = content
    )
}
