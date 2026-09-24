package com.emmanuelyator.mydistro.feature.driver.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroTopBar
import com.emmanuelyator.mydistro.core.designsystem.theme.Dimens
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing

@Composable
fun DriverHelpSupportRoute(
    onBack: () -> Unit
) {
    DriverHelpSupportScreen(onBack = onBack)
}

@Composable
fun DriverHelpSupportScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            MyDistroTopBar(
                title = "Help & Support",
                onBack = onBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.screenPadding, vertical = Spacing.lg)
        ) {
            // Contact Support Card
            Text(
                text = "Contact Support",
                style = MaterialTheme.typography.labelLarge,
                color = MyDistroTheme.colors.textTertiary,
                modifier = Modifier.padding(bottom = Spacing.sm)
            )

            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SupportItem(
                        icon = Icons.Outlined.Phone,
                        title = "Call Distributor",
                        subtitle = "+254 700 000 000",
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+254700000000"))
                            context.startActivity(intent)
                        }
                    )
                    HorizontalDivider(color = MyDistroTheme.colors.border, thickness = Dimens.hairline)
                    SupportItem(
                        icon = Icons.Outlined.Email,
                        title = "Email Support",
                        subtitle = "support@mydistro.com",
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:support@mydistro.com"))
                            context.startActivity(intent)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xxl))

            // Resources Card
            Text(
                text = "Resources",
                style = MaterialTheme.typography.labelLarge,
                color = MyDistroTheme.colors.textTertiary,
                modifier = Modifier.padding(bottom = Spacing.sm)
            )

            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SupportItem(
                        icon = Icons.Outlined.MenuBook,
                        title = "Driver Manual",
                        subtitle = "Guidelines for delivery processes",
                        onClick = { /* Open web view or PDF */ }
                    )
                    HorizontalDivider(color = MyDistroTheme.colors.border, thickness = Dimens.hairline)
                    SupportItem(
                        icon = Icons.Outlined.BugReport,
                        title = "Report an Issue",
                        subtitle = "Found a bug? Let us know",
                        onClick = { /* Open issue reporting screen */ }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(Spacing.xxl))

            // App Version Info
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "MyDistro Driver App v1.0.0 (Build 42)\n© 2026 MyDistro. All rights reserved.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MyDistroTheme.colors.textTertiary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun SupportItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(Spacing.lg))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MyDistroTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(Spacing.xxs))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MyDistroTheme.colors.textSecondary
            )
        }
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = MyDistroTheme.colors.textTertiary
        )
    }
}
