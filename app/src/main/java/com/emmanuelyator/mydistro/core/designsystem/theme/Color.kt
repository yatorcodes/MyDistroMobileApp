package com.emmanuelyator.mydistro.core.designsystem.theme

import androidx.compose.ui.graphics.Color

/**
 * Raw palette. Screens should not reference these directly — read them through
 * [MyDistroTheme] / MaterialTheme.colorScheme or [MyDistroColors] so the brand
 * can be retuned in one place.
 *
 * Brand orange/navy standardized 2026-09 on the Driver Login screen's values —
 * Stitch generated slightly different hex for "brand orange" and "brand navy"
 * on every screen (Splash: #F96822/#0D1D33, Role Selection: #FF5400,
 * Driver Login: #F95B12/#0E131F). Driver Login was picked as canonical since
 * it's the most brand-heavy screen and its values were internally consistent.
 * If any old screens were already built against the previous Orange500/Navy900
 * values, they need a pass to match.
 */

// Brand navy — headers, primary surfaces, primary text
internal val Navy900 = Color(0xFF0E131F)   // was #0B1930
internal val Navy800 = Color(0xFF151D2C)   // navyCard — was #122540
internal val Navy700 = Color(0xFF1B3557)   // unchanged; revisit if a screen needs it
internal val Navy100 = Color(0xFFD6DEE9)   // unchanged; no dark-hero screen has used this yet

// Additional navy shades Driver Login needs that the old 4-step scale didn't cover
internal val NavyInput = Color(0xFF131B2A)   // form field backgrounds on hero screens
internal val NavyBorder = Color(0xFF232D42)  // form field / card borders on hero screens

// Brand orange — calls to action only, so it keeps its weight
internal val Orange500 = Color(0xFFF95B12)   // was #FF641F
internal val Orange600 = Color(0xFFE04E0B)   // hover/pressed — was #E5551A
internal val Orange100 = Color(0xFFFFE3D6)   // unchanged; not yet confirmed against a real screen
internal val Orange50 = Color(0xFFFFF3ED)    // unchanged; not yet confirmed against a real screen

// Neutrals — confirmed against Role Selection's default-Tailwind usage
internal val White = Color(0xFFFFFFFF)
internal val Gray50 = Color(0xFFF7F8FA)      // close match to Role Selection's #FAFBFD device bg
internal val Gray100 = Color(0xFFEEF1F5)
internal val Gray200 = Color(0xFFE3E7ED)     // close match to #E5E7EB card border
internal val Gray400 = Color(0xFF9AA6B8)     // close match to Tailwind slate-400 #94A3B8
internal val Gray500 = Color(0xFF6B7A90)     // close match to Tailwind slate-500 #64748B
internal val Gray700 = Color(0xFF3D4B5C)

// Semantic status colours, each with a tinted background for badges
// Not yet cross-checked against a real status-badge screen (Trip List,
// Order Status) — worth pasting one of those next to confirm these.
internal val Success500 = Color(0xFF12A150)
internal val Success50 = Color(0xFFE7F7EE)
internal val Info500 = Color(0xFF2563EB)
internal val Info50 = Color(0xFFE8EFFD)
internal val Warning500 = Color(0xFFD97706)
internal val Warning50 = Color(0xFFFEF3E2)
internal val Danger500 = Color(0xFFDC2626)
internal val Danger50 = Color(0xFFFDECEC)