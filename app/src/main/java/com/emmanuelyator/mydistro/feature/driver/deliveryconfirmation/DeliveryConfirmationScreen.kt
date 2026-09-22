package com.emmanuelyator.mydistro.feature.driver.deliveryconfirmation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emmanuelyator.mydistro.core.common.AppError
import com.emmanuelyator.mydistro.core.designsystem.component.ErrorState
import com.emmanuelyator.mydistro.core.designsystem.component.LoadingState
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroCard
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroPrimaryButton
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroTopBar
import com.emmanuelyator.mydistro.core.designsystem.component.OtpInput
import com.emmanuelyator.mydistro.core.designsystem.component.TripProgressBar
import com.emmanuelyator.mydistro.core.designsystem.theme.Dimens
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.PillShape
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing
import com.emmanuelyator.mydistro.core.designsystem.theme.StatusBarIcons
import com.emmanuelyator.mydistro.core.model.StopStatus
import com.emmanuelyator.mydistro.feature.driver.data.MockTripData

@Composable
fun DeliveryConfirmationRoute(
    onBack: () -> Unit,
    onDone: () -> Unit,
    viewModel: DeliveryConfirmationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DeliveryConfirmationContent(
        uiState = uiState,
        onBack = onBack,
        onOtpChange = viewModel::onOtpChange,
        onConfirm = viewModel::onConfirm,
        onDone = onDone
    )
}

@Composable
private fun DeliveryConfirmationContent(
    uiState: DeliveryConfirmationUiState,
    onBack: () -> Unit,
    onOtpChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    StatusBarIcons(dark = true)
    val keyboard = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            MyDistroTopBar(
                title = if (uiState.isConfirmed) "Delivery Confirmed" else "Close Delivery",
                // Back is hidden after success so the driver uses the explicit
                // "Done" action and cannot land back on a stale OTP form.
                onBack = if (uiState.isConfirmed) null else onBack
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                uiState.loadError != null -> ErrorState(
                    error = AppError.Validation(uiState.loadError)
                )

                uiState.isLoading -> LoadingState(message = "Loading delivery…")

                uiState.isConfirmed -> SuccessBody(uiState = uiState, onDone = onDone)

                else -> FormBody(
                    uiState = uiState,
                    onOtpChange = onOtpChange,
                    onConfirm = {
                        keyboard?.hide()
                        onConfirm()
                    }
                )
            }
        }
    }
}

@Composable
private fun FormBody(
    uiState: DeliveryConfirmationUiState,
    onOtpChange: (String) -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            // Scaffold already supplies the navigation-bar inset; only the
            // keyboard needs handling here.
            .imePadding()
            .padding(horizontal = Dimens.screenPadding)
            .padding(bottom = Spacing.xxl)
    ) {
        DeliveryContextCard(uiState = uiState)

        Spacer(Modifier.height(Spacing.xxl))

        Text(
            text = "Enter OTP",
            style = MaterialTheme.typography.titleLarge,
            color = MyDistroTheme.colors.textPrimary
        )
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = "The customer will read you a $OTP_LENGTH-digit code. " +
                "Enter it to close this delivery.",
            style = MaterialTheme.typography.bodyMedium,
            color = MyDistroTheme.colors.textSecondary
        )

        Spacer(Modifier.height(Spacing.lg))

        OtpInput(
            value = uiState.otp,
            onValueChange = onOtpChange,
            length = OTP_LENGTH,
            enabled = !uiState.isSubmitting,
            isError = uiState.otpError != null,
            onDone = onConfirm
        )

        AnimatedVisibility(
            visible = uiState.otpError != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = uiState.otpError.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = Spacing.sm)
            )
        }

        Spacer(Modifier.height(Spacing.xl))

        MockValidationNotice()

        Spacer(Modifier.height(Spacing.xl))

        MyDistroPrimaryButton(
            text = if (uiState.isSubmitting) "Confirming…" else "Confirm Delivery",
            onClick = onConfirm,
            enabled = uiState.canSubmit,
            loading = uiState.isSubmitting
        )

        Spacer(Modifier.height(Spacing.md))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = MyDistroTheme.colors.textTertiary,
                modifier = Modifier.size(Dimens.iconSm)
            )
            Spacer(Modifier.width(Spacing.xs))
            Text(
                text = "Check the goods are in good condition before confirming.",
                style = MaterialTheme.typography.bodySmall,
                color = MyDistroTheme.colors.textTertiary,
                textAlign = TextAlign.Center
            )
        }
    }
}

/** Which delivery this is, so the driver cannot close the wrong stop. */
@Composable
private fun DeliveryContextCard(uiState: DeliveryConfirmationUiState) {
    val stop = uiState.stop ?: return

    MyDistroCard(containerColor = MyDistroTheme.colors.neutralContainer) {
        Text(
            text = stop.orderNumber?.let { "Order $it" } ?: "Warehouse intake",
            style = MaterialTheme.typography.titleMedium,
            color = MyDistroTheme.colors.textPrimary
        )
        Spacer(Modifier.height(Spacing.xxs))
        Text(
            text = stop.location.name,
            style = MaterialTheme.typography.bodyMedium,
            color = MyDistroTheme.colors.textSecondary
        )
        Text(
            text = stop.location.address,
            style = MaterialTheme.typography.bodySmall,
            color = MyDistroTheme.colors.textTertiary
        )
        if (stop.contactName != null) {
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = "Receiving: ${stop.contactName}",
                style = MaterialTheme.typography.bodySmall,
                color = MyDistroTheme.colors.textSecondary
            )
        }
    }
}

@Composable
private fun SuccessBody(
    uiState: DeliveryConfirmationUiState,
    onDone: () -> Unit
) {
    // Settles the tick in from slightly small — enough to register as
    // confirmation without turning into a celebration animation.
    var settled by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (settled) 1f else 0.82f,
        animationSpec = tween(durationMillis = 380),
        label = "successTickScale"
    )
    LaunchedEffect(Unit) { settled = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.screenPadding, vertical = Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(Spacing.xxxl))

        Box(
            modifier = Modifier
                .size(96.dp)
                .scale(scale)
                .clip(PillShape)
                .background(MyDistroTheme.colors.successContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = MyDistroTheme.colors.success,
                modifier = Modifier.size(52.dp)
            )
        }

        Spacer(Modifier.height(Spacing.xl))

        Text(
            text = "Delivery confirmed",
            style = MaterialTheme.typography.headlineMedium,
            color = MyDistroTheme.colors.textPrimary,
            textAlign = TextAlign.Center
        )
        val confirmedStop = uiState.stop
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = if (confirmedStop?.orderNumber != null) {
                "Order ${confirmedStop.orderNumber} at " +
                    "${confirmedStop.location.name} is closed."
            } else {
                "This stop is closed."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MyDistroTheme.colors.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(Spacing.xxl))

        val trip = uiState.trip
        if (trip != null) {
            MyDistroCard {
                Text(
                    text = "Trip progress",
                    style = MaterialTheme.typography.titleSmall,
                    color = MyDistroTheme.colors.textPrimary
                )
                Spacer(Modifier.height(Spacing.md))
                TripProgressBar(
                    completed = trip.completedStopCount,
                    total = trip.totalStopCount
                )
                val next = trip.stops.firstOrNull { it.status != StopStatus.COMPLETED }
                Spacer(Modifier.height(Spacing.md))
                Text(
                    text = next?.let { "Next stop: ${it.location.name} · ETA ${it.etaLabel}" }
                        ?: "All stops on this trip are complete.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MyDistroTheme.colors.textSecondary
                )
            }
        }

        Spacer(Modifier.weight(1f))

        MyDistroPrimaryButton(text = "Back to trip", onClick = onDone)
    }
}

/**
 * The OTP is compared against a constant on-device. Saying so prevents anyone
 * mistaking this screen for a working proof of delivery.
 */
@Composable
private fun MockValidationNotice() {
    MyDistroCard(
        containerColor = MyDistroTheme.colors.warningContainer,
        borderColor = MyDistroTheme.colors.warning.copy(alpha = 0.3f),
        contentPadding = Spacing.md
    ) {
        Text(
            text = "MOCK VALIDATION",
            style = MaterialTheme.typography.labelSmall,
            color = MyDistroTheme.colors.warning
        )
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = "No OTP has been sent to anyone. This build checks the code " +
                "on-device against ${MockTripData.DEMO_OTP}. Real codes will be " +
                "generated and verified by the backend.",
            style = MaterialTheme.typography.bodySmall,
            color = MyDistroTheme.colors.textSecondary
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F8FA, heightDp = 900)
@Composable
private fun DeliveryConfirmationFormPreview() {
    val trip = MockTripData.deliveryTripInProgress
    MyDistroTheme {
        DeliveryConfirmationContent(
            uiState = DeliveryConfirmationUiState(
                trip = trip,
                stop = trip.stops[1],
                otp = "1842"
            ),
            onBack = {},
            onOtpChange = {},
            onConfirm = {},
            onDone = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F8FA, heightDp = 900)
@Composable
private fun DeliveryConfirmationSuccessPreview() {
    val trip = MockTripData.deliveryTripInProgress
    MyDistroTheme {
        DeliveryConfirmationContent(
            uiState = DeliveryConfirmationUiState(
                trip = trip,
                stop = trip.stops[1],
                otp = MockTripData.DEMO_OTP,
                isConfirmed = true
            ),
            onBack = {},
            onOtpChange = {},
            onConfirm = {},
            onDone = {}
        )
    }
}
