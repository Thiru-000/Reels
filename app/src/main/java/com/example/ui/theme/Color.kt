package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Official Instagram Brand Palette
val InstaWhite = Color(0xFFFFFFFF)
val InstaBlack = Color(0xFF000000)
val InstaTextPrimary = Color(0xFF262626)
val InstaTextSecondary = Color(0xFF737373)
val InstaTextLight = Color(0xFF8E8E8E)
val InstaBorder = Color(0xFFDBDBDB)
val InstaDivider = Color(0xFFEFEFEF)
val InstaBg = Color(0xFFFFFFFF)
val InstaSecondaryBg = Color(0xFFFAFAFA)
val InstaCardBg = Color(0xFFFFFFFF)

val InstaBlue = Color(0xFF0095F6)
val InstaBluePressed = Color(0xFF1877F2)
val InstaRed = Color(0xFFED4956)
val InstaGreen = Color(0xFF00BA88)
val InstaAmber = Color(0xFFFF9500)

// Official Instagram Story Gradient
val InstagramStoryGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFFFBAA47),
        Color(0xFFD91A46),
        Color(0xFFA60F93)
    )
)

val InstagramStoryRainbow = Brush.sweepGradient(
    listOf(
        Color(0xFFFBAA47),
        Color(0xFFD91A46),
        Color(0xFFA60F93),
        Color(0xFFFBAA47)
    )
)

val InstagramButtonGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF833AB4),
        Color(0xFFFD1D1D),
        Color(0xFFFCB045)
    )
)
