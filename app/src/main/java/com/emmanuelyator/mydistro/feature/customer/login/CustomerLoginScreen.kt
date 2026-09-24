package com.emmanuelyator.mydistro.feature.customer.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emmanuelyator.mydistro.R
import com.emmanuelyator.mydistro.core.designsystem.component.DiamondLogo
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.StatusBarIcons
import com.emmanuelyator.mydistro.feature.auth.data.DemoCustomerCredentials
import androidx.compose.foundation.BorderStroke

/**
 * Peak height of the navy panel's upward chevron into the hero photo.
 */
private val ChevronPeak = 28.dp
private val PillShape = RoundedCornerShape(50)

/**
 * The generated photo has a tall sky; faces sit in the lower half.
 * Bias Y toward the bottom so Crop frames the sellers, not empty sky.
 */
private val HeroFaceAlignment = BiasAlignment(horizontalBias = 0f, verticalBias = 0.55f)

@Composable
fun CustomerLoginRoute(
    onLoginSuccess: () -> Unit,
    onForgotPassword: () -> Unit,
    onCreateAccount: () -> Unit,
    onSignUp: () -> Unit,
    viewModel: CustomerLoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                CustomerLoginEvent.LoginSucceeded -> onLoginSuccess()
            }
        }
    }

    CustomerLoginScreen(
        uiState = uiState,
        onIdentifierChange = viewModel::onIdentifierChange,
        onPasswordChange = viewModel::onPasswordChange,
        onRememberMeChange = viewModel::onRememberMeChange,
        onLoginClick = viewModel::onSubmit,
        onForgotPasswordClick = onForgotPassword,
        onCreateAccountClick = onCreateAccount,
        onSignUpClick = onSignUp
    )
}

@Composable
fun CustomerLoginScreen(
    uiState: CustomerLoginUiState,
    onIdentifierChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRememberMeChange: (Boolean) -> Unit,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    StatusBarIcons(dark = false)

    var passwordVisible by remember { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current
    val navy = MyDistroTheme.colors.heroSurface
    // Panel lets faces show through; fields stay more opaque for typing.
    val panelGlass = navy.copy(alpha = 0.78f)
    val fieldFill = navy.copy(alpha = 0.92f)
    val fieldBorder = Color.White.copy(alpha = 0.50f)
    val muted = Color.White.copy(alpha = 0.78f)

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.img_customer_login),
            contentDescription = "Kenyan retailers and hardware sellers",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = HeroFaceAlignment
        )

        // Soft bottom fade so the translucent panel still feels grounded
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.00f to Color.Transparent,
                            0.28f to Color.Transparent,
                            0.55f to navy.copy(alpha = 0.25f),
                            1.00f to navy.copy(alpha = 0.55f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            // Larger photo band so faces sit above the chevron
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.42f)
                    .padding(top = 12.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DiamondLogo(modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "MyDistro",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.3).sp
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.58f)
                    .clip(ChevronTopShape(ChevronPeak))
                    .background(panelGlass)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(top = ChevronPeak + 10.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DiamondLogo(modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "MyDistro",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Welcome Back",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Log in to your account",
                    color = muted,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(20.dp))

                CustomerTextField(
                    value = uiState.identifier,
                    onValueChange = onIdentifierChange,
                    placeholder = "Phone number or email",
                    leadingIcon = Icons.Filled.PhoneAndroid,
                    keyboardType = KeyboardType.Text,
                    enabled = !uiState.isSubmitting,
                    isError = uiState.identifierError != null,
                    borderColor = fieldBorder,
                    containerColor = fieldFill
                )
                if (uiState.identifierError != null) {
                    FieldError(uiState.identifierError)
                }

                Spacer(Modifier.height(12.dp))

                CustomerTextField(
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
                    borderColor = fieldBorder,
                    containerColor = fieldFill
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
                        color = Color(0xFFFFB4A8),
                        fontSize = 13.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    )
                }

                Spacer(Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            onRememberMeChange(!uiState.rememberMe)
                        }
                    ) {
                        Checkbox(
                            checked = uiState.rememberMe,
                            onCheckedChange = onRememberMeChange,
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.secondary,
                                uncheckedColor = Color.White.copy(alpha = 0.7f),
                                checkmarkColor = Color.White
                            )
                        )
                        Text(
                            text = "Remember me",
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    }
                    Text(
                        text = "Forgot password?",
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable(
                                enabled = !uiState.isSubmitting,
                                onClick = onForgotPasswordClick
                            )
                            .padding(vertical = 8.dp)
                    )
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        keyboard?.hide()
                        onLoginClick()
                    },
                    enabled = uiState.canSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = PillShape,
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

                Spacer(Modifier.height(18.dp))

                OrDivider()

                Spacer(Modifier.height(18.dp))

                OutlinedButton(
                    onClick = onCreateAccountClick,
                    enabled = !uiState.isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = PillShape,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.75f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text(
                        text = "Create an account",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Don't have an account? ",
                        color = muted,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Sign up",
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(
                            enabled = !uiState.isSubmitting,
                            onClick = onSignUpClick
                        )
                    )
                }

                Spacer(Modifier.height(12.dp))

                PrototypeHint()
            }
        }
    }
}

/**
 * Upward chevron along the top edge of the navy panel — matches the mockup's
 * "mountain peak" cut into the hero photo.
 */
private class ChevronTopShape(
    private val peakHeight: Dp
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val peakPx = with(density) { peakHeight.toPx() }
        val path = Path().apply {
            moveTo(0f, peakPx)
            lineTo(size.width * 0.50f, 0f)
            lineTo(size.width, peakPx)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
private fun OrDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = Color.White.copy(alpha = 0.25f)
        )
        Text(
            text = "or",
            color = Color.White.copy(alpha = 0.55f),
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = Color.White.copy(alpha = 0.25f)
        )
    }
}

@Composable
private fun FieldError(message: String) {
    Text(
        text = message,
        fontSize = 12.sp,
        color = Color(0xFFFFB4A8),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp, start = 4.dp)
    )
}

@Composable
private fun PrototypeHint() {
    Text(
        text = "PROTOTYPE — ${DemoCustomerCredentials.PHONE} / ${DemoCustomerCredentials.PASSWORD}",
        color = Color.White.copy(alpha = 0.40f),
        fontSize = 11.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun CustomerTextField(
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
    borderColor: Color,
    containerColor: Color
) {
    val iconTint = Color.White.copy(alpha = 0.85f)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        isError = isError,
        placeholder = {
            Text(placeholder, color = Color.White.copy(alpha = 0.45f))
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
        shape = PillShape,
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
            cursorColor = MaterialTheme.colorScheme.secondary
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true, heightDp = 860, widthDp = 390)
@Composable
private fun CustomerLoginPreview() {
    MyDistroTheme {
        CustomerLoginScreen(
            uiState = CustomerLoginUiState(),
            onIdentifierChange = {},
            onPasswordChange = {},
            onRememberMeChange = {},
            onLoginClick = {},
            onForgotPasswordClick = {},
            onCreateAccountClick = {},
            onSignUpClick = {}
        )
    }
}
