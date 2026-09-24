package com.emmanuelyator.mydistro.feature.driver.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emmanuelyator.mydistro.R
import com.emmanuelyator.mydistro.core.designsystem.component.DiamondLogo
import com.emmanuelyator.mydistro.core.designsystem.component.PhotoHeroBackground
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.StatusBarIcons
import com.emmanuelyator.mydistro.feature.auth.data.DemoCredentials

@Composable
fun DriverLoginRoute(
    onLoginSuccess: () -> Unit,
    onForgotPassword: () -> Unit,
    viewModel: DriverLoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                DriverLoginEvent.LoginSucceeded -> onLoginSuccess()
            }
        }
    }

    DriverLoginScreen(
        uiState = uiState,
        onCredentialChange = viewModel::onIdentifierChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLoginClick = viewModel::onSubmit,
        onForgotPasswordClick = onForgotPassword
    )
}

/**
 * Driver Login matching the production mockup: highway truck photo, bottom
 * fade, brand lockup, then the form sitting in the darkened lower third.
 */
@Composable
fun DriverLoginScreen(
    uiState: DriverLoginUiState,
    onCredentialChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    StatusBarIcons(dark = false)

    var passwordVisible by remember { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current
    val fieldOnPhoto = Color.White.copy(alpha = 0.18f)
    val fieldBorder = Color.White.copy(alpha = 0.55f)
    val muted = Color.White.copy(alpha = 0.78f)

    PhotoHeroBackground(
        imageRes = R.drawable.img_driver_login_truck,
        contentDescription = "Delivery truck on a highway at sunset",
        bottomScrimAlpha = 0.88f,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp)
                .padding(top = 28.dp, bottom = 28.dp)
        ) {
            // Brand lockup — top centre, matching the mockup
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DiamondLogo(modifier = Modifier.size(34.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "MyDistro",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = (-0.3).sp
                )
            }

            Spacer(Modifier.weight(1f))

            // Form block sits in the faded lower third
            Text(
                text = "Driver Login",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = (-0.3).sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Access your trips and deliveries",
                fontSize = 15.sp,
                color = muted
            )

            Spacer(Modifier.height(28.dp))

            LoginTextField(
                value = uiState.identifier,
                onValueChange = onCredentialChange,
                placeholder = "Phone number or email",
                leadingIcon = Icons.Filled.PhoneAndroid,
                keyboardType = KeyboardType.Text,
                enabled = !uiState.isSubmitting,
                isError = uiState.identifierError != null,
                containerColor = fieldOnPhoto,
                borderColor = fieldBorder
            )
            if (uiState.identifierError != null) {
                FieldError(uiState.identifierError)
            }

            Spacer(Modifier.height(14.dp))

            LoginTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                placeholder = "Password",
                leadingIcon = Icons.Filled.Lock,
                keyboardType = KeyboardType.Password,
                isPassword = true,
                passwordVisible = passwordVisible,
                onToggleVisibility = { passwordVisible = !passwordVisible },
                enabled = !uiState.isSubmitting,
                isError = uiState.passwordError != null,
                containerColor = fieldOnPhoto,
                borderColor = fieldBorder
            )
            if (uiState.passwordError != null) {
                FieldError(uiState.passwordError)
            }

            AnimatedVisibility(
                visible = uiState.formError != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = uiState.formError.orEmpty(),
                    fontSize = 13.sp,
                    color = Color(0xFFFFB4A8),
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    keyboard?.hide()
                    onLoginClick()
                },
                enabled = uiState.canSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.45f),
                    disabledContentColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.8f)
                )
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                } else {
                    Text(
                        text = "Login",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Forgot password?",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.85f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .clickable(
                        enabled = !uiState.isSubmitting,
                        onClick = onForgotPasswordClick
                    ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            PrototypeHint()
        }
    }
}

@Composable
private fun FieldError(message: String) {
    Text(
        text = message,
        fontSize = 12.sp,
        color = Color(0xFFFFB4A8),
        modifier = Modifier.padding(top = 6.dp, start = 4.dp)
    )
}

@Composable
private fun PrototypeHint() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.10f))
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = "PROTOTYPE — MOCK AUTH",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Use ${DemoCredentials.PHONE} / ${DemoCredentials.PASSWORD}",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.75f)
            )
        }
    }
}

@Composable
private fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onToggleVisibility: (() -> Unit)? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    containerColor: Color,
    borderColor: Color
) {
    val iconTint = Color.White.copy(alpha = 0.85f)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        isError = isError,
        placeholder = {
            Text(placeholder, color = Color.White.copy(alpha = 0.55f))
        },
        leadingIcon = {
            Icon(leadingIcon, contentDescription = null, tint = iconTint)
        },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { onToggleVisibility?.invoke() }) {
                    Icon(
                        imageVector = if (passwordVisible) {
                            Icons.Filled.VisibilityOff
                        } else {
                            Icons.Filled.Visibility
                        },
                        contentDescription = if (passwordVisible) {
                            "Hide password"
                        } else {
                            "Show password"
                        },
                        tint = iconTint
                    )
                }
            }
        } else {
            null
        },
        visualTransformation = if (isPassword && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            disabledContainerColor = containerColor,
            errorContainerColor = containerColor,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = borderColor,
            errorBorderColor = Color(0xFFFFB4A8),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            disabledTextColor = Color.White.copy(alpha = 0.6f),
            cursorColor = MaterialTheme.colorScheme.secondary
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true, heightDp = 800, widthDp = 390)
@Composable
private fun DriverLoginPreview() {
    MyDistroTheme {
        DriverLoginScreen(
            uiState = DriverLoginUiState(
                identifier = "0712345678",
                password = "driver123"
            ),
            onCredentialChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onForgotPasswordClick = {}
        )
    }
}
