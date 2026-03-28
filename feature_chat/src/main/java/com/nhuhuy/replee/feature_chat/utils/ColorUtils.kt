package com.nhuhuy.replee.feature_chat.utils

import androidx.compose.ui.graphics.Color
import com.nhuhuy.replee.core.data.data_store.ChatColor

fun ChatColor.toPrimaryColor(): Color {
    return when (this) {
        ChatColor.SAPPHIRE -> Color(0xFF38BDF8)
        ChatColor.EMERALD -> Color(0xFF006D3B)
        ChatColor.VIVID_ORANGE -> Color(0xFF8B5000)
        ChatColor.ROYAL_PURPLE -> Color(0xFF6750A4)
        ChatColor.CRIMSON -> Color(0xFFB3261E)
        ChatColor.SLATE_TEAL -> Color(0xFF006A6A)
    }
}