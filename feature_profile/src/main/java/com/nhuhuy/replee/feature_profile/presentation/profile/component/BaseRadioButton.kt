package com.nhuhuy.replee.feature_profile.presentation.profile.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BaseRadioButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable{
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically,
    ){
        RadioButton(
            onClick = onClick,
            selected = selected
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}