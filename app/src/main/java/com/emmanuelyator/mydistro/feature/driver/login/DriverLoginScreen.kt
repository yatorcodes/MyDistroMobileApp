package com.emmanuelyator.mydistro.feature.driver.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emmanuelyator.mydistro.core.designsystem.component.HeroBackground
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroLogo
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroPasswordField
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroPrimaryButton
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroTextButton
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroTextField
import com.emmanuelyator.mydistro.core.designsystem.theme.Dimens
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.PillShape
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing
import com.emmanuelyator.mydistro.core.designsystem.theme.StatusBarIcons
import com.emmanuelyator.mydistro.feature.auth.data.DemoCredentials

/**
 * Stateful entry point. Keeps the ViewModel out of [DriverLoginContent] so the
 * layout stays previewable and testable with plain values.
 */
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

    DriverLoginContent(
        uiState = uiState,
        onIdentifierChange = viewModel::onIdentifierChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSubmit = viewModel::onSubmit,
        onForgotPassword = onForgotPassword
    )
}

@Composable
private fun DriverLoginContent(
    uiState: DriverLoginUiState,
    onIdentifierChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onForgotPassword: () -> Unit,
    modifier: Modifier = Modifier
) {
    StatusBarIcons(dark = false)
    val keyboard = LocalSoftwareKeyboardController.current

    // imePadding keeps the form above the keyboard, and verticalScroll means the
    // whole screen still reaches every field on short devices.
    HeroBackground(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MyDistroTheme.colors.heroSurface.copy(alpha = 0.55f),
                            MyDistroTheme.colors.heroSurface.copy(alpha = 0.96f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .widthIn(max = Dimens.maxContentWidth)
                .align(Alignment.TopCenter)
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = Dimens.screenPadding)
                .padding(top = Spacing.xxl, bottom = Spacing.xxl)
        ) {
            MyDistroLogo(
                markSize = 26.dp,
                wordmarkColor = MyDistroTheme.colors.onHeroSurface
            )

            Spacer(Modifier.height(Spacing.huge))

            DriverBadge()

            Spacer(Modifier.height(Spacing.xl))

            Text(
                text = "Driver Login",
                style = MaterialTheme.typography.headlineLarge,
                color = MyDistroTheme.colors.onHeroSurface
            )
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = "Access your trips and deliveries",
                style = MaterialTheme.typography.bodyMedium,
                color = MyDistroTheme.colors.onHeroSurfaceVariant
            )

            Spacer(Modifier.height(Spacing.xxl))

            MyDistroTextField(
                value = uiState.identifier,
                onValueChange = onIdentifierChange,
                placeholder = "Phone number or email",
                leadingIcon = Icons.Outlined.PersonOutline,
                errorMessage = uiState.identifierError,
                enabled = !uiState.isSubmitting,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )

            Spacer(Modifier.height(Spacing.md))

            MyDistroPasswordField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                placeholder = "Password",
                leadingIcon = Icons.Outlined.Lock,
                errorMessage = uiState.passwordError,
                enabled = !uiState.isSubmitting,
                imeAction = ImeAction.Done,
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboard?.hide()
                        onSubmit()
                    }
                )
            )

            AnimatedVisibility(
                visible = uiState.formError != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FormErrorMessage(message = uiState.formError.orEmpty())
            }

            Spacer(Modifier.height(Spacing.xl))

            MyDistroPrimaryButton(
                text = if (uiState.isSubmitting) "Signing in…" else "Login",
                onClick = {
                    keyboard?.hide()
                    onSubmit()
                },
                enabled = uiState.canSubmit,
                loading = uiState.isSubmitting
            )

            Spacer(Modifier.height(Spacing.xs))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                MyDistroTextButton(
                    text = "Forgot password?",
                    onClick = onForgotPassword,
                    enabled = !uiState.isSubmitting,
                    accent = true
                )
            }

            Spacer(Modifier.height(Spacing.lg))

            PrototypeNotice()

            Spacer(Modifier.height(Spacing.xl))

            Text(
                text = "Driver accounts are created by your distributor.\nSecure · Fast · Reliable",
                style = MaterialTheme.typography.bodySmall,
                color = MyDistroTheme.colors.onHeroSurfaceVariant.copy(alpha = 0.75f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/** Reinforces which of the two mobile roles the user is signing into. */
@Composable
private fun DriverBadge() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(PillShape)
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.LocalShipping,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(26.dp)
            )
        }
        Column {
            Text(
                text = "For drivers",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = "MyDistro Logistics",
                style = MaterialTheme.typography.titleSmall,
                color = MyDistroTheme.colors.onHeroSurface
            )
        }
    }
}

@Composable
private fun FormErrorMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Spacing.md)
            .clip(MaterialTheme.shapes.extraSmall)
            .background(MyDistroTheme.colors.danger.copy(alpha = 0.16f))
            .padding(Spacing.md)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )
    }
}

/**
 * States plainly that no backend is involved yet, and shows the demo
 * credentials. Better an honest label than a login screen that looks real.
 */
@Composable
private fun PrototypeNotice() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(Color.White.copy(alpha = 0.08f))
            .padding(Spacing.md)
    ) {
        Column {
            Text(
                text = "PROTOTYPE — NOT CONNECTED TO A BACKEND",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = "Credentials are checked on-device against demo values. " +
                    "Sign in with ${DemoCredentials.PHONE} / ${DemoCredentials.PASSWORD}.",
                style = MaterialTheme.typography.bodySmall,
                color = MyDistroTheme.colors.onHeroSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DriverLoginPreview() {
    MyDistroTheme {
        DriverLoginContent(
            uiState = DriverLoginUiState(identifier = "0712345678", password = "driver123"),
            onIdentifierChange = {},
            onPasswordChange = {},
            onSubmit = {},
            onForgotPassword = {}
        )
    }
}

@Preview(showBackground = true, name = "Login — error")
@Composable
private fun DriverLoginErrorPreview() {
    MyDistroTheme {
        DriverLoginContent(
            uiState = DriverLoginUiState(
                identifier = "0712345678",
                password = "wrong",
                formError = "Incorrect phone number or password."
            ),
            onIdentifierChange = {},
            onPasswordChange = {},
            onSubmit = {},
            onForgotPassword = {}
        )
    }
}
