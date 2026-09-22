package com.emmanuelyator.mydistro.feature.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emmanuelyator.mydistro.R
import com.emmanuelyator.mydistro.core.designsystem.component.HeroBackground
import com.emmanuelyator.mydistro.core.designsystem.theme.Dimens
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing
import com.emmanuelyator.mydistro.core.designsystem.theme.StatusBarIcons
import kotlinx.coroutines.delay

private const val SPLASH_DWELL_MILLIS = 1_400L

/**
 * Brand entry point. Holds for a beat, fades the lockup in, then hands off.
 *
 * The dwell is a deliberate brand moment rather than a loading screen — once
 * there is a real session check to perform, [onFinished] should be driven by
 * that instead of a timer.
 */
@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    StatusBarIcons(dark = false)

    var visible by remember { mutableStateOf(false) }
    val contentAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 650),
        label = "splashFade"
    )

    LaunchedEffect(Unit) {
        visible = true
        delay(SPLASH_DWELL_MILLIS)
        onFinished()
    }

    HeroBackground(modifier = modifier.fillMaxSize()) {
        // Darkens the lower half so the wordmark and tagline stay legible
        // whatever artwork sits behind them.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            MyDistroTheme.colors.heroSurface.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(horizontal = Dimens.screenPadding, vertical = Spacing.xxxl)
                .alpha(contentAlpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(R.drawable.ic_mydistro_logo),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
            Spacer(Modifier.height(Spacing.lg))
            Text(
                text = "MyDistro",
                style = MaterialTheme.typography.displaySmall,
                color = MyDistroTheme.colors.onHeroSurface
            )
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = "Stronger supply chains.\nA brighter Kenya.",
                style = MaterialTheme.typography.bodyLarge,
                color = MyDistroTheme.colors.onHeroSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Text(
            text = "Manufacturers  ·  Distributors  ·  Hardware  ·  Drivers",
            style = MaterialTheme.typography.bodySmall,
            color = MyDistroTheme.colors.onHeroSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = Dimens.screenPadding, vertical = Spacing.xxl)
                .alpha(contentAlpha)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    MyDistroTheme {
        SplashScreen(onFinished = {})
    }
}
