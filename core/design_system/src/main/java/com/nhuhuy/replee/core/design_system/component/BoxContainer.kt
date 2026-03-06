package com.nhuhuy.replee.core.design_system.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nhuhuy.replee.core.design_system.hideKeyboardOnTap

@Composable
fun BoxContainer(
    content: @Composable BoxScope.() -> Unit
){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .hideKeyboardOnTap(),
        contentAlignment = Alignment.Center
    ){
        content()
    }
}