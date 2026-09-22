package com.emmanuelyator.mydistro.core.designsystem.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Brand colour scheme.
 *
 * Navy is `primary` (headers, emphasis, primary text) and orange is `secondary`
 * so that Material components which default to `primary` — switches, focused
 * field outlines, selection handles — stay navy, and orange is only spent where
 * we explicitly ask for it. That keeps the call-to-action colour meaningful.
 */
private val MyDistroColorScheme = lightColorScheme(
    primary = Navy900,
    onPrimary = White,
    primaryContainer = Navy800,
    onPrimaryContainer = Navy100,

    secondary = Orange500,
    onSecondary = White,
    secondaryContainer = Orange100,
    onSecondaryContainer = Orange600,

    tertiary = Info500,
    onTertiary = White,

    background = Gray50,
    onBackground = Navy900,

    surface = White,
    onSurface = Navy900,
    surfaceVariant = Gray100,
    onSurfaceVariant = Gray500,

    outline = Gray400,
    outlineVariant = Gray200,

    error = Danger500,
    onError = White,
    errorContainer = Danger50,
    onErrorContainer = Danger500,

    scrim = Navy900
)

/**
 * The app is intentionally light-only: the reference design has no dark
 * treatment, and shipping an auto-derived dark scheme would produce unreviewed
 * screens. Dark mode should be added as a designed variant, not inferred — at
 * which point this function gains a `darkTheme` parameter and a second scheme.
 *
 * Material You dynamic colour is deliberately off: wallpaper-derived colours
 * would replace the navy/orange brand identity on Android 12+.
 */
@Composable
fun MyDistroTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalMyDistroColors provides LightMyDistroColors) {
        MaterialTheme(
            colorScheme = MyDistroColorScheme,
            typography = MyDistroTypography,
            shapes = MyDistroShapes,
            content = content
        )
    }
}

/** Accessor for brand colours outside Material's own scheme. */
object MyDistroTheme {
    val colors: MyDistroColors
        @Composable @ReadOnlyComposable get() = LocalMyDistroColors.current
}

/**
 * Switches status-bar icon colour per screen. Navy "hero" screens (splash,
 * login) need light icons; the light content screens need dark ones. Without
 * this, the clock and battery vanish against one background or the other.
 */
@Composable
fun StatusBarIcons(dark: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return
    SideEffect {
        val window = (view.context as? Activity)?.window ?: return@SideEffect
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = dark
    }
}
