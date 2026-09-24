package com.emmanuelyator.mydistro.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Brand colours that Material 3's [androidx.compose.material3.ColorScheme] has no
 * slot for: status semantics, hairline borders and the navy "hero" surfaces used
 * on the splash, login and trip-detail headers.
 *
 * Read via `MyDistroTheme.colors` rather than importing the raw palette.
 */
@Immutable
data class MyDistroColors(
    val heroSurface: Color,
    val heroSurfaceVariant: Color,
    val onHeroSurface: Color,
    val onHeroSurfaceVariant: Color,
    val border: Color,
    val borderStrong: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val success: Color,
    val successContainer: Color,
    val info: Color,
    val infoContainer: Color,
    val warning: Color,
    val warningContainer: Color,
    val danger: Color,
    val dangerContainer: Color,
    val neutral: Color,
    val neutralContainer: Color,
    // Added for form fields on dark hero screens (Driver Login, etc.) —
    // previously hardcoded as raw hex inline in DriverLoginScreen.
    val inputBackground: Color,
    val inputBorder: Color
)

internal val LightMyDistroColors = MyDistroColors(
    heroSurface = Navy900,
    heroSurfaceVariant = Navy800,
    onHeroSurface = White,
    onHeroSurfaceVariant = Navy100,
    border = Gray200,
    borderStrong = Gray400,
    textPrimary = Navy900,
    textSecondary = Gray500,
    textTertiary = Gray400,
    success = Success500,
    successContainer = Success50,
    info = Info500,
    infoContainer = Info50,
    warning = Warning500,
    warningContainer = Warning50,
    danger = Danger500,
    dangerContainer = Danger50,
    neutral = Gray500,
    neutralContainer = Gray100,
    inputBackground = NavyInput,
    inputBorder = NavyBorder
)

internal val LocalMyDistroColors = staticCompositionLocalOf { LightMyDistroColors }