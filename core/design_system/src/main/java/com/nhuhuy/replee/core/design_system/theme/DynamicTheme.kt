package com.nhuhuy.replee.core.design_system.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.rememberDynamicMaterialThemeState

/*import com.materialkolor.DynamicMaterialTheme*/

@Composable
fun DynamicRepleeTheme(
    seedColor: Color,
    isDark: Boolean = true,
    content: @Composable () -> Unit
) {
    val state = rememberDynamicMaterialThemeState(
        primary = seedColor,
        isAmoled = true,
        isDark = isDark,
    )

    DynamicMaterialTheme(
        state = state,
        content = content,
        animate = true
    )
}