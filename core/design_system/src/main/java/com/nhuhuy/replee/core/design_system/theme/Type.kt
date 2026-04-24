package com.nhuhuy.replee.core.design_system.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import com.nhuhuy.replee.core.design_system.R

val InterFont = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold)
)

val FigtreeFont = FontFamily(
    Font(R.font.figtree_regular, FontWeight.Normal),
    Font(R.font.figtree_medium, FontWeight.Medium),
    Font(R.font.figtree_semibold, FontWeight.SemiBold),
    Font(R.font.figtree_bold, FontWeight.Bold)
)

val defaultPlatformTextStyle = PlatformTextStyle(includeFontPadding = false)
val defaultLineHeightStyle = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.None
)
val baseline = Typography()
val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(
        fontFamily = InterFont,
        platformStyle = defaultPlatformTextStyle,
        lineHeightStyle = defaultLineHeightStyle
    ),
    displayMedium = baseline.displayMedium.copy(
        fontFamily = InterFont,
        platformStyle = defaultPlatformTextStyle
    ),
    displaySmall = baseline.displaySmall.copy(
        fontFamily = InterFont,
        platformStyle = defaultPlatformTextStyle
    ),

    headlineLarge = baseline.headlineLarge.copy(
        fontFamily = InterFont,
        fontWeight = FontWeight.SemiBold,
        platformStyle = defaultPlatformTextStyle
    ),
    headlineMedium = baseline.headlineMedium.copy(
        fontFamily = InterFont,
        fontWeight = FontWeight.SemiBold,
        platformStyle = defaultPlatformTextStyle
    ),
    headlineSmall = baseline.headlineSmall.copy(
        fontFamily = InterFont,
        fontWeight = FontWeight.SemiBold,
        platformStyle = defaultPlatformTextStyle
    ),
    titleLarge = baseline.titleLarge.copy(
        fontFamily = InterFont,
        fontWeight = FontWeight.SemiBold,
        platformStyle = defaultPlatformTextStyle
    ),
    titleMedium = baseline.titleMedium.copy(
        fontFamily = InterFont,
        fontWeight = FontWeight.Medium,
        platformStyle = defaultPlatformTextStyle
    ),
    titleSmall = baseline.titleSmall.copy(
        fontFamily = InterFont,
        fontWeight = FontWeight.Medium,
        platformStyle = defaultPlatformTextStyle
    ),
    bodyLarge = baseline.bodyLarge.copy(
        fontFamily = FigtreeFont,
        platformStyle = defaultPlatformTextStyle
    ),
    bodyMedium = baseline.bodyMedium.copy(
        fontFamily = FigtreeFont,
        platformStyle = defaultPlatformTextStyle
    ),
    bodySmall = baseline.bodySmall.copy(
        fontFamily = FigtreeFont,
        platformStyle = defaultPlatformTextStyle
    ),
    labelLarge = baseline.labelLarge.copy(
        fontFamily = FigtreeFont,
        fontWeight = FontWeight.Medium,
        platformStyle = defaultPlatformTextStyle
    ),
    labelMedium = baseline.labelMedium.copy(
        fontFamily = FigtreeFont,
        fontWeight = FontWeight.Medium,
        platformStyle = defaultPlatformTextStyle
    ),
    labelSmall = baseline.labelSmall.copy(
        fontFamily = FigtreeFont,
        fontWeight = FontWeight.Medium,
        platformStyle = defaultPlatformTextStyle
    )
)
