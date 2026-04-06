package com.nhuhuy.replee.feature_chat.utils

import androidx.compose.ui.graphics.Color
import com.nhuhuy.replee.core.data.data_store.SeedColor

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