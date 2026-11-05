package com.dreamrecall.diary.core.theme

import androidx.compose.ui.graphics.Color

val NightDeep = Color(0xFF0D0B1E)
val NightRaised = Color(0xFF141031)
val NightAbyss = Color(0xFF0A0818)
val SurfaceCard = Color(0xFF1A1535)
val SurfaceSheet = Color(0xFF161230)
val SurfaceSecondary = Color(0xFF221E3F)
val BorderSoft = Color(0x14FFFFFF)
val BorderStrong = Color(0xFF3A3160)
val Primary = Color(0xFF8E7CFF)
val PrimaryLight = Color(0xFFB9A6FF)
val Accent = Color(0xFFFFD6E8)
val AccentCool = Color(0xFF7FD8FF)
val TextPrimary = Color(0xFFE7E4F5)
val TextBody = Color(0xFFD9D3F0)
val TextDim = Color(0xFFA79FD4)
val TextMuted = Color(0xFF8D86B8)
val TextFaint = Color(0xFF6E6890)
val Danger = Color(0xFFFF8FA3)

val VividnessRamp = listOf(
    Color(0xFF4C3F8F),
    Color(0xFF6B59C4),
    Color(0xFF8E7CFF),
    Color(0xFFC3AFFF),
    Color(0xFFFFD6E8)
)

fun vividnessColor(value: Int): Color = VividnessRamp[value.coerceIn(1, VividnessRamp.size) - 1]
