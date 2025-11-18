package com.example.android_tv_home_screen_frontend.ui.theme

import android.graphics.Color

/**
 * Executive Gray theme constants and helpers for TV UI.
 * Provides color palette, spacing, typography scales and common dims.
 */
object ExecutiveGrayTheme {
    // Colors
    val Primary = Color.parseColor("#374151")
    val Secondary = Color.parseColor("#9CA3AF")
    val Background = Color.parseColor("#F9FAFB")
    val Surface = Color.parseColor("#FFFFFF")
    val Text = Color.parseColor("#111827")
    val Success = Color.parseColor("#059669")
    val Error = Color.parseColor("#DC2626")
    val FocusRing = Color.parseColor("#9CA3AF") // subtle silver
    val FocusGlow = Color.parseColor("#D1D5DB") // lighter gray for glow

    // Dims
    const val SafeMargin = 32 // dp
    const val CardWidth = 220 // dp
    const val CardHeight = 330 // dp
    const val CardRadius = 12 // dp
    const val CardSpacing = 24 // dp
    const val RowSpacing = 40 // dp
    const val TopBarHeight = 72 // dp
    const val InfoPaneHeight = 240 // dp

    // Typography (sp)
    const val TitleXL = 42
    const val TitleL = 32
    const val TitleM = 26
    const val BodyL = 22
    const val BodyM = 20
    const val Meta = 18
}
