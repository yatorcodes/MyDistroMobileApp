package com.emmanuelyator.mydistro.feature.driver.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroTopBar
import com.emmanuelyator.mydistro.core.designsystem.theme.Dimens
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing

@Composable
fun DriverProfileRoute(
    onSignOut: () -> Unit,
    onEditProfile: () -> Unit,
    onNotifications: () -> Unit,
    onPrivacySecurity: () -> Unit,
    onHelpSupport: () -> Unit
) {
    DriverProfileScreen(
        onSignOut = onSignOut,
        onEditProfile = onEditProfile,
        onNotifications = onNotifications,
        onPrivacySecurity = onPrivacySecurity,
        onHelpSupport = onHelpSupport
    )
}

@Composable
fun DriverProfileScreen(
    onSignOut: () -> Unit,
    onEditProfile: () -> Unit,
    onNotifications: () -> Unit,
    onPrivacySecurity: () -> Unit,
    onHelpSupport: () -> Unit
) {
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                profileImageUri = uri
            }
        }
    )

    Scaffold(
        topBar = {
            MyDistroTopBar(title = "Profile")
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
            // Profile Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(Dimens.avatarLg)
                        .clip(CircleShape)
                        .background(MyDistroTheme.colors.heroSurface)
                        .clickable {
                            photoPickerLauncher.launch(
                                androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (profileImageUri != null) {
                        AsyncImage(
                            model = profileImageUri,
                            contentDescription = "Profile picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // "AM" placeholder to match Trips screen
                        Text(
                            text = "AM",
                            style = MaterialTheme.typography.titleLarge,
                            color = MyDistroTheme.colors.onHeroSurface
                        )
                    }
                    
                    // Small camera icon overlay to indicate it's clickable
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PhotoCamera,
                            contentDescription = "Upload photo",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(Spacing.lg))

                Column {
                    Text(
                        text = "Alex Mutua",
                        style = MaterialTheme.typography.titleLarge,
                        color = MyDistroTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(Spacing.xxs))
                    Text(
                        text = "+254 712 345 678",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MyDistroTheme.colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xxl))

            // Vehicle Section
            Text(
                text = "Vehicle Information",
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.lg),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocalShipping,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.lg))
                    Column {
                        Text(
                            text = "KDA 421X",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MyDistroTheme.colors.textPrimary
                        )
                        Text(
                            text = "Isuzu FRR • 10 Tonnes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MyDistroTheme.colors.textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xxl))

            // Settings Section
            Text(
                text = "Settings",
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
                    SettingsItem(
                        icon = Icons.Outlined.Edit,
                        title = "Edit Profile",
                        onClick = onEditProfile
                    )
                    HorizontalDivider(color = MyDistroTheme.colors.border, thickness = Dimens.hairline)
                    SettingsItem(
                        icon = Icons.Outlined.NotificationsNone,
                        title = "Notifications",
                        onClick = onNotifications
                    )
                    HorizontalDivider(color = MyDistroTheme.colors.border, thickness = Dimens.hairline)
                    SettingsItem(
                        icon = Icons.Outlined.Security,
                        title = "Privacy & Security",
                        onClick = onPrivacySecurity
                    )
                    HorizontalDivider(color = MyDistroTheme.colors.border, thickness = Dimens.hairline)
                    SettingsItem(
                        icon = Icons.AutoMirrored.Outlined.HelpOutline,
                        title = "Help & Support",
                        onClick = onHelpSupport
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(Spacing.xxl))

            // Sign Out Button
            OutlinedButton(
                onClick = onSignOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.buttonHeight),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Logout,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(Spacing.sm))
                Text("Sign Out", style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
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
            tint = MyDistroTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.width(Spacing.lg))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MyDistroTheme.colors.textPrimary,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = MyDistroTheme.colors.textTertiary
        )
    }
}
