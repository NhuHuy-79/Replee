package com.nhuhuy.replee.feature_chat.utils

import androidx.compose.ui.graphics.Color
import com.nhuhuy.replee.core.model.settings.SeedColor
import com.nhuhuy.replee.feature_chat.R

fun SeedColor.toPrimaryColor(): Color {
    return when (this) {
        SeedColor.SAPPHIRE -> Color(0xFF38BDF8)
        SeedColor.EMERALD -> Color(0xFF006D3B)
        SeedColor.VIVID_ORANGE -> Color(0xFF8B5000)
        SeedColor.ROYAL_PURPLE -> Color(0xFF6750A4)
        SeedColor.CRIMSON -> Color(0xFFB3261E)
        SeedColor.SLATE_TEAL -> Color(0xFF006A6A)
    }
}

fun SeedColor.toDrawable(): Int {
    return when (this) {
        SeedColor.SAPPHIRE -> R.drawable.bg_sapphire
        SeedColor.EMERALD -> R.drawable.bg_emerald
        SeedColor.VIVID_ORANGE -> R.drawable.bg_oragne
        SeedColor.ROYAL_PURPLE -> R.drawable.bg_purple
        SeedColor.CRIMSON -> R.drawable.bg_red
        SeedColor.SLATE_TEAL -> R.drawable.bg_steal
    }
}
