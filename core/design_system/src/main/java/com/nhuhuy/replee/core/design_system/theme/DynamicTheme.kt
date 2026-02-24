package com.nhuhuy.replee.core.design_system.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/*import com.materialkolor.DynamicMaterialTheme*/

@Composable
fun DynamicRepleeTheme(
    seedColor: Color,
    isDark: Boolean = true,
    content: @Composable () -> Unit
) {
    /* DynamicMaterialTheme(
         seedColor = seedColor,
         isDark = isDark,
         animate = true,
         isAmoled = true,
         content = content
     )*/
    content
}