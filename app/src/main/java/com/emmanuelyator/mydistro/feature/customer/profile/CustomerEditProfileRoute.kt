package com.emmanuelyator.mydistro.feature.customer.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroPrimaryButton
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroTextField
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroTopBar
import com.emmanuelyator.mydistro.core.designsystem.theme.Dimens
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing
import kotlinx.coroutines.launch

@Composable
fun CustomerEditProfileRoute(
    onBack: () -> Unit
) {
    CustomerEditProfileScreen(onBack = onBack)
}

@Composable
fun CustomerEditProfileScreen(
    onBack: () -> Unit
) {
    // Initial mock values matching the customer profile
    var firstName by remember { mutableStateOf("Jane") }
    var lastName by remember { mutableStateOf("Kamau") }
    var phone by remember { mutableStateOf("+254 700 123 456") }
    var email by remember { mutableStateOf("jane.kamau@example.com") }
    var shopName by remember { mutableStateOf("Jane's Mini Mart") }
    var customerId by remember { mutableStateOf("CUST-9011") }

    var isSaving by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            MyDistroTopBar(
                title = "Edit Profile",
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
            MyDistroTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = "First Name",
                placeholder = "Enter your first name",
                leadingIcon = Icons.Outlined.Person,
                imeAction = ImeAction.Next
            )
            
            Spacer(modifier = Modifier.height(Spacing.lg))
            
            MyDistroTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = "Last Name",
                placeholder = "Enter your last name",
                leadingIcon = Icons.Outlined.Person,
                imeAction = ImeAction.Next
            )
            
            Spacer(modifier = Modifier.height(Spacing.lg))
            
            MyDistroTextField(
                value = phone,
                onValueChange = { phone = it },
                label = "Phone Number",
                placeholder = "Enter your phone number",
                leadingIcon = Icons.Outlined.Phone,
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            )
            
            Spacer(modifier = Modifier.height(Spacing.lg))
            
            MyDistroTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email Address",
                placeholder = "Enter your email address",
                leadingIcon = Icons.Outlined.Email,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            MyDistroTextField(
                value = shopName,
                onValueChange = { shopName = it },
                label = "Shop Name",
                placeholder = "Enter your shop's name",
                leadingIcon = Icons.Outlined.Storefront,
                imeAction = ImeAction.Done
            )

            Spacer(modifier = Modifier.height(Spacing.lg))
            
            // Read-only field for Customer ID
            MyDistroTextField(
                value = customerId,
                onValueChange = { },
                label = "Customer ID (Read Only)",
                placeholder = "Customer ID",
                leadingIcon = Icons.Outlined.Badge,
                enabled = false
            )

            Spacer(modifier = Modifier.height(Spacing.xxl))
            
            MyDistroPrimaryButton(
                text = "Save Changes",
                loading = isSaving,
                onClick = {
                    scope.launch {
                        isSaving = true
                        // Simulate network call
                        kotlinx.coroutines.delay(1000)
                        isSaving = false
                        snackbarHostState.showSnackbar("Profile updated successfully")
                    }
                }
            )
            
            // Extra spacing at the bottom since the user can scroll
            Spacer(modifier = Modifier.height(Spacing.xxl))
        }
    }
}
