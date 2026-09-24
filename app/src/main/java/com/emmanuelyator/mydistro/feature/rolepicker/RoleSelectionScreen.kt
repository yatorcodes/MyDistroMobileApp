package com.emmanuelyator.mydistro.feature.rolepicker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Factory
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.Warehouse
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emmanuelyator.mydistro.core.designsystem.component.DiamondLogo
import com.emmanuelyator.mydistro.core.designsystem.component.HeroBackground
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.StatusBarIcons
import com.emmanuelyator.mydistro.core.model.UserRole

/**
 * Role Selection — navy hero theme shared with Splash / Login, so the
 * onboarding flow stays continuous without relying on a role-specific photo.
 *
 * When a licensed "everyone in the chain" photo is ready, swap
 * [HeroBackground] for [PhotoHeroBackground] with that asset.
 */
@Composable
fun RoleSelectionScreen(
    onRoleSelected: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    StatusBarIcons(dark = false)

    HeroBackground(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 22.dp)
                .padding(top = 24.dp, bottom = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DiamondLogo(modifier = Modifier.size(30.dp))
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "MyDistro",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = (-0.3).sp
                )
            }

            Spacer(Modifier.height(36.dp))

            Text(
                text = "Welcome to MyDistro",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = (-0.3).sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Choose your role to continue",
                fontSize = 15.sp,
                color = Color.White.copy(alpha = 0.78f)
            )

            Spacer(Modifier.height(28.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RoleCard(
                    title = "Driver",
                    subtitle = "Manage trips and deliveries",
                    icon = Icons.Outlined.LocalShipping,
                    badgeColor = Color(0xFF60A5FA),
                    onClick = { onRoleSelected(UserRole.DRIVER) }
                )
                RoleCard(
                    title = "Customer",
                    subtitle = "Order products and track",
                    icon = Icons.Outlined.Storefront,
                    badgeColor = Color(0xFF34D399),
                    onClick = { onRoleSelected(UserRole.CUSTOMER) }
                )
                RoleCard(
                    title = "Distributor",
                    subtitle = "Manage orders and stock",
                    icon = Icons.Outlined.Warehouse,
                    badgeColor = Color(0xFFC084FC),
                    onClick = { onRoleSelected(UserRole.DISTRIBUTOR) }
                )
                RoleCard(
                    title = "Factory / Manufacturer",
                    subtitle = "Manage production and stock",
                    icon = Icons.Outlined.Factory,
                    badgeColor = MaterialTheme.colorScheme.secondary,
                    onClick = { onRoleSelected(UserRole.FACTORY) }
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Need help? Contact support",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.55f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RoleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badgeColor: Color,
    onClick: () -> Unit
) {
    val glass = Color.White.copy(alpha = 0.10f)
    val glassBorder = Color.White.copy(alpha = 0.22f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(glass)
            .border(1.dp, glassBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(badgeColor.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.65f)
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.55f)
        )
    }
}

@Preview(showBackground = true, heightDp = 800, widthDp = 390)
@Composable
private fun RoleSelectionPreview() {
    MyDistroTheme {
        RoleSelectionScreen(onRoleSelected = {})
    }
}
