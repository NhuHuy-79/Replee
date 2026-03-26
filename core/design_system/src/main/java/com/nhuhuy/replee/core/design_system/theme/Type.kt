package com.nhuhuy.replee.core.design_system.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.nhuhuy.replee.core.design_system.R

val InterFont = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
)

val FigtreeFont = FontFamily(
    Font(R.font.figtree_regular, FontWeight.Normal),
    Font(R.font.figtree_medium, FontWeight.Medium),
    Font(R.font.figtree_semibold, FontWeight.SemiBold),
)

// Default Material 3 typography values
val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = InterFont),
    displayMedium = baseline.displayMedium.copy(fontFamily = InterFont),
    displaySmall = baseline.displaySmall.copy(fontFamily = InterFont),

    headlineLarge = baseline.headlineLarge.copy(fontFamily = InterFont),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = InterFont),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = InterFont),

    titleLarge = baseline.titleLarge.copy(fontFamily = InterFont),
    titleMedium = baseline.titleMedium.copy(fontFamily = InterFont),
    titleSmall = baseline.titleSmall.copy(fontFamily = InterFont),

    bodyLarge = baseline.bodyLarge.copy(fontFamily = FigtreeFont),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = FigtreeFont),
    bodySmall = baseline.bodySmall.copy(fontFamily = FigtreeFont),

    labelLarge = baseline.labelLarge.copy(fontFamily = FigtreeFont),
    labelMedium = baseline.labelMedium.copy(fontFamily = FigtreeFont),
    labelSmall = baseline.labelSmall.copy(fontFamily = FigtreeFont),
)

