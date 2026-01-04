package com.nhuhuy.replee.core.design_system.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun UserImage(
    userName: String ,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier.size(56.dp).background(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = if (userName.isEmpty()){
                "U"
            } else userName.toCharArray().first().uppercase(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}