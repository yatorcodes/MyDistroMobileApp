package com.emmanuelyator.mydistro.core.designsystem.theme

import androidx.compose.ui.graphics.Color

/**
 * Raw palette. Screens should not reference these directly — read them through
 * [MyDistroTheme] / MaterialTheme.colorScheme or [MyDistroColors] so the brand
 * can be retuned in one place.
 */

// Brand navy — headers, primary surfaces, primary text
internal val Navy900 = Color(0xFF0B1930)
internal val Navy800 = Color(0xFF122540)
internal val Navy700 = Color(0xFF1B3557)
internal val Navy100 = Color(0xFFD6DEE9)

// Brand orange — calls to action only, so it keeps its weight
internal val Orange500 = Color(0xFFFF641F)
internal val Orange600 = Color(0xFFE5551A)
internal val Orange100 = Color(0xFFFFE3D6)
internal val Orange50 = Color(0xFFFFF3ED)

// Neutrals
internal val White = Color(0xFFFFFFFF)
internal val Gray50 = Color(0xFFF7F8FA)
internal val Gray100 = Color(0xFFEEF1F5)
internal val Gray200 = Color(0xFFE3E7ED)
internal val Gray400 = Color(0xFF9AA6B8)
internal val Gray500 = Color(0xFF6B7A90)
internal val Gray700 = Color(0xFF3D4B5C)

// Semantic status colours, each with a tinted background for badges
internal val Success500 = Color(0xFF12A150)
internal val Success50 = Color(0xFFE7F7EE)
internal val Info500 = Color(0xFF2563EB)
internal val Info50 = Color(0xFFE8EFFD)
internal val Warning500 = Color(0xFFD97706)
internal val Warning50 = Color(0xFFFEF3E2)
internal val Danger500 = Color(0xFFDC2626)
internal val Danger50 = Color(0xFFFDECEC)
