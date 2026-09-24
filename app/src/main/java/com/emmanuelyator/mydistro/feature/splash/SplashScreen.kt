package com.emmanuelyator.mydistro.feature.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Factory
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emmanuelyator.mydistro.R
import com.emmanuelyator.mydistro.core.designsystem.component.DiamondLogo
import com.emmanuelyator.mydistro.core.designsystem.component.PhotoHeroBackground
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.StatusBarIcons
import kotlinx.coroutines.delay

private const val SPLASH_DWELL_MILLIS = 2_200L

/**
 * Brand splash matching the production mockup:
 * full-bleed truck photo, diamond logo + tagline, audience row at the bottom.
 */
@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    StatusBarIcons(dark = false)

    LaunchedEffect(Unit) {
        delay(SPLASH_DWELL_MILLIS)
        onFinished()
    }

    PhotoHeroBackground(
        imageRes = R.drawable.img_splash_truck,
        contentDescription = "MyDistro distribution truck at an industrial plant",
        bottomScrimAlpha = 0.72f,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(top = 36.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // -------------------- Branding --------------------
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 12.dp)
            ) {
                DiamondLogo(modifier = Modifier.size(56.dp))

                Spacer(Modifier.height(14.dp))

                Text(
                    text = "MyDistro",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.6).sp
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = "Stronger Supply Chains.\nA Brighter Kenya.",
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.widthIn(max = 260.dp)
                )
            }

            Spacer(Modifier.weight(1f))

            // -------------------- Audience footer --------------------
            AudienceFooter(
                items = listOf(
                    AudienceItem("Manufacturers", Icons.Outlined.Factory),
                    AudienceItem("Distributors", Icons.Outlined.LocalShipping),
                    AudienceItem("Hardware\nCustomers", Icons.Outlined.Storefront),
                    AudienceItem("Drivers", Icons.Outlined.Person)
                )
            )
        }
    }
}

private data class AudienceItem(
    val label: String,
    val icon: ImageVector
)

@Composable
private fun AudienceFooter(items: List<AudienceItem>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = item.label,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp,
                    maxLines = 2
                )
            }
            if (index != items.lastIndex) {
                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .width(1.dp)
                        .background(Color.White.copy(alpha = 0.45f))
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 800, widthDp = 390)
@Composable
private fun SplashPreview() {
    MyDistroTheme {
        SplashScreen(onFinished = {})
    }
}
