package com.emmanuelyator.mydistro.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing

/**
 * Lockup of the brand mark plus the "MyDistro" wordmark.
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
        DiamondLogo(
            modifier = Modifier
                .size(markSize)
                .semantics {
                    contentDescription = if (showWordmark) "" else "MyDistro"
                }
        )
        if (showWordmark) {
            Text(text = "MyDistro", style = textStyle, color = wordmarkColor)
        }
    }
}

/**
 * Geometric diamond mark from the brand SVG (viewBox 0 0 64 64).
 */
@Composable
fun DiamondLogo(modifier: Modifier = Modifier) {
    val orange = MaterialTheme.colorScheme.secondary
    Canvas(modifier = modifier) {
        val scale = size.width / 64f

        fun point(x: Float, y: Float) = Offset(x * scale, y * scale)

        val path1 = Path().apply {
            moveTo(point(22f, 6f).x, point(22f, 6f).y)
            lineTo(point(48f, 32f).x, point(48f, 32f).y)
            lineTo(point(38f, 42f).x, point(38f, 42f).y)
            lineTo(point(12f, 16f).x, point(12f, 16f).y)
            close()
        }
        val path2 = Path().apply {
            moveTo(point(30f, 46f).x, point(30f, 46f).y)
            lineTo(point(40f, 56f).x, point(40f, 56f).y)
            lineTo(point(50f, 46f).x, point(50f, 46f).y)
            lineTo(point(40f, 36f).x, point(40f, 36f).y)
            close()
        }
        val path3 = Path().apply {
            moveTo(point(14f, 26f).x, point(14f, 26f).y)
            lineTo(point(24f, 36f).x, point(24f, 36f).y)
            lineTo(point(32f, 28f).x, point(32f, 28f).y)
            lineTo(point(22f, 18f).x, point(22f, 18f).y)
            close()
        }

        drawPath(path1, color = orange)
        drawPath(path2, color = Color(0xFFFF8D4D))
        drawPath(path3, color = Color(0xFFFA7736))
    }
}

/**
 * Full-bleed photo with a bottom fade so white UI stays legible.
 * [imageRes] must be JPG / PNG / WEBP — never a layer-list or shape drawable.
 */
@Composable
fun PhotoHeroBackground(
    @DrawableRes imageRes: Int,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    bottomScrimAlpha: Float = 0.82f,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.00f to Color.Transparent,
                            0.38f to Color.Transparent,
                            0.68f to Color.Black.copy(alpha = bottomScrimAlpha * 0.45f),
                            1.00f to Color.Black.copy(alpha = bottomScrimAlpha)
                        )
                    )
                )
        )
        content()
    }
}

/** Fallback navy gradient when a photo asset is unavailable. */
@Composable
fun HeroBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val navy = MyDistroTheme.colors.heroSurface
    val navyMid = MyDistroTheme.colors.heroSurfaceVariant
    val warm = Color(0xFF7A3418)
    val orangeGlow = Color(0xFFF95B12)

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(navy, navyMid, warm),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height)
                    )
                )
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
