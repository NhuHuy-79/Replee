package com.nhuhuy.replee.core.design_system.component

import androidx.annotation.StringRes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun CommonButton(
    modifier: Modifier,
    @StringRes res: Int,
    enabled: Boolean = true,
    onClick: () -> Unit
){
    Button(
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Text(
            text = stringResource(res),
            style = MaterialTheme.typography.labelLarge
        )
    }
}