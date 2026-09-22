package com.emmanuelyator.mydistro.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.emmanuelyator.mydistro.R
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing

/**
 * Lockup of the brand mark plus the "MyDistro" wordmark. Every screen uses this
 * rather than drawing the logo itself, so a future licensed logo is a one-file
 * swap.
 */
@Composable
fun MyDistroLogo(
    modifier: Modifier = Modifier,
    markSize: Dp = 28.dp,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    wordmarkColor: Color = MyDistroTheme.colors.textPrimary,
    showWordmark: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_mydistro_logo),
            contentDescription = if (showWordmark) null else "MyDistro",
            modifier = Modifier.size(markSize)
        )
        if (showWordmark) {
            Text(text = "MyDistro", style = textStyle, color = wordmarkColor)
        }
    }
}

/**
 * Navy hero backdrop for splash and login.
 *
 * Painted with Compose brushes. Do not load `bg_hero_navy.xml` via
 * [painterResource] — that drawable is a layer-list / shape, and Compose only
 * accepts VectorDrawable and raster formats (PNG / JPG / WEBP). Loading it
 * crashes at runtime with:
 * "Only VectorDrawables and rasterized asset types are supported".
 *
 * When a licensed photograph is available, replace the drawBehind block with an
 * Image of that asset; keep this composable name so call sites stay unchanged.
 */
@Composable
fun HeroBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val navy = MyDistroTheme.colors.heroSurface
    val navyMid = MyDistroTheme.colors.heroSurfaceVariant
    val warm = Color(0xFF7A3418)
    val orangeGlow = Color(0xFFFF641F)

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                // Base navy → warm diagonal, matching the design's dusk feel.
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(navy, navyMid, warm),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height)
                    )
                )
                // Soft orange glow near the lower-right "horizon".
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            orangeGlow.copy(alpha = 0.45f),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.72f, size.height * 0.78f),
                        radius = size.minDimension * 0.85f
                    )
                )
            }
    ) {
        content()
    }
}
