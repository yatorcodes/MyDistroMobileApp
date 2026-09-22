package com.emmanuelyator.mydistro.feature.rolepicker

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroCard
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroLogo
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroTextButton
import com.emmanuelyator.mydistro.core.designsystem.theme.Dimens
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing
import com.emmanuelyator.mydistro.core.designsystem.theme.StatusBarIcons
import com.emmanuelyator.mydistro.core.model.UserRole

/** Presentation data for one role row. */
private data class RoleOption(
    val role: UserRole,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accent: Color
)

/**
 * Entry fork. Driver and Customer continue into the app; Distributor and
 * Factory are shown because operators who install the wrong app need to be told
 * where to go, and tapping them explains rather than silently failing.
 */
@Composable
fun RoleSelectionScreen(
    onRoleSelected: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    StatusBarIcons(dark = true)

    val options = roleOptions()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                // Caps the column so it stays readable rather than stretched on
                // tablets and unfolded foldables.
                .widthIn(max = Dimens.maxContentWidth)
                .align(Alignment.TopCenter)
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = Dimens.screenPadding)
                .padding(top = Spacing.xxxl, bottom = Spacing.xxl)
        ) {
            MyDistroLogo(markSize = 32.dp)
            Spacer(Modifier.height(Spacing.xxxl))

            Text(
                text = "Welcome to MyDistro",
                style = MaterialTheme.typography.headlineLarge,
                color = MyDistroTheme.colors.textPrimary
            )
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = "Choose your role to continue",
                style = MaterialTheme.typography.bodyMedium,
                color = MyDistroTheme.colors.textSecondary
            )
            Spacer(Modifier.height(Spacing.xxl))

            options.forEach { option ->
                RoleCard(
                    option = option,
                    onClick = { onRoleSelected(option.role) },
                    modifier = Modifier.padding(bottom = Spacing.md)
                )
            }

            Spacer(Modifier.height(Spacing.lg))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Need help?",
                    style = MaterialTheme.typography.bodySmall,
                    color = MyDistroTheme.colors.textSecondary
                )
                MyDistroTextButton(text = "Contact support", onClick = {}, accent = true)
            }
        }
    }
}

@Composable
private fun RoleCard(
    option: RoleOption,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    MyDistroCard(modifier = modifier, onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(option.accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = null,
                    tint = option.accent,
                    modifier = Modifier.size(Dimens.iconLg)
                )
            }
            Spacer(Modifier.width(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MyDistroTheme.colors.textPrimary
                )
                Spacer(Modifier.height(Spacing.xxs))
                Text(
                    text = option.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MyDistroTheme.colors.textSecondary
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = MyDistroTheme.colors.textTertiary
            )
        }
    }
}

@Composable
private fun roleOptions(): List<RoleOption> = listOf(
    RoleOption(
        role = UserRole.DRIVER,
        title = "Driver",
        description = "Manage trips and deliveries",
        icon = Icons.Outlined.LocalShipping,
        accent = MyDistroTheme.colors.info
    ),
    RoleOption(
        role = UserRole.CUSTOMER,
        title = "Customer",
        description = "Order products and track delivery",
        icon = Icons.Outlined.Storefront,
        accent = MyDistroTheme.colors.success
    ),
    RoleOption(
        role = UserRole.DISTRIBUTOR,
        title = "Distributor",
        description = "Manage orders and stock on the web console",
        icon = Icons.Outlined.Warehouse,
        accent = MaterialTheme.colorScheme.primary
    ),
    RoleOption(
        role = UserRole.FACTORY,
        title = "Factory / Manufacturer",
        description = "Manage production and stock on the web console",
        icon = Icons.Outlined.Factory,
        accent = MaterialTheme.colorScheme.secondary
    )
)

@Preview(showBackground = true, backgroundColor = 0xFFF7F8FA)
@Composable
private fun RoleSelectionPreview() {
    MyDistroTheme {
        RoleSelectionScreen(onRoleSelected = {})
    }
}
