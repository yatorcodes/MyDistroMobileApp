package com.emmanuelyator.mydistro.feature.driver.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroPasswordField
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroPrimaryButton
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroTopBar
import com.emmanuelyator.mydistro.core.designsystem.theme.Dimens
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing
import kotlinx.coroutines.launch

@Composable
fun DriverPrivacySecurityRoute(
    onBack: () -> Unit
) {
    DriverPrivacySecurityScreen(onBack = onBack)
}

@Composable
fun DriverPrivacySecurityScreen(
    onBack: () -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var biometricEnabled by remember { mutableStateOf(true) }
    var locationTrackingEnabled by remember { mutableStateOf(true) }

    var isSaving by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            MyDistroTopBar(
                title = "Privacy & Security",
                onBack = onBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.screenPadding, vertical = Spacing.lg)
        ) {
            // Settings Toggles Section
            Text(
                text = "Security Preferences",
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
                    ToggleSettingItem(
                        title = "Biometric Login",
                        description = "Use fingerprint or face to login securely",
                        checked = biometricEnabled,
                        onCheckedChange = { biometricEnabled = it }
                    )
                    
                    ToggleSettingItem(
                        title = "Background Location",
                        description = "Allow distro to track location for route optimization when app is minimized",
                        checked = locationTrackingEnabled,
                        onCheckedChange = { locationTrackingEnabled = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xxl))

            // Change Password Section
            Text(
                text = "Change Password",
                style = MaterialTheme.typography.labelLarge,
                color = MyDistroTheme.colors.textTertiary,
                modifier = Modifier.padding(bottom = Spacing.sm)
            )
            
            MyDistroPasswordField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = "Current Password",
                placeholder = "Enter current password",
                leadingIcon = Icons.Outlined.Lock,
                imeAction = ImeAction.Next
            )
            
            Spacer(modifier = Modifier.height(Spacing.lg))
            
            MyDistroPasswordField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = "New Password",
                placeholder = "Enter new password",
                leadingIcon = Icons.Outlined.Lock,
                imeAction = ImeAction.Next
            )
            
            Spacer(modifier = Modifier.height(Spacing.lg))
            
            MyDistroPasswordField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm New Password",
                placeholder = "Confirm new password",
                leadingIcon = Icons.Outlined.Lock,
                imeAction = ImeAction.Done
            )

            Spacer(modifier = Modifier.height(Spacing.xxl))
            
            MyDistroPrimaryButton(
                text = "Update Security Settings",
                loading = isSaving,
                onClick = {
                    scope.launch {
                        isSaving = true
                        kotlinx.coroutines.delay(1000)
                        isSaving = false
                        
                        if (newPassword.isNotEmpty() && newPassword != confirmPassword) {
                            snackbarHostState.showSnackbar("New passwords do not match!")
                        } else {
                            currentPassword = ""
                            newPassword = ""
                            confirmPassword = ""
                            snackbarHostState.showSnackbar("Security settings updated securely")
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun ToggleSettingItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MyDistroTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(Spacing.xxs))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MyDistroTheme.colors.textSecondary
            )
        }
        Spacer(modifier = Modifier.width(Spacing.lg))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.surface,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MyDistroTheme.colors.textSecondary,
                uncheckedTrackColor = MyDistroTheme.colors.border
            )
        )
    }
}
