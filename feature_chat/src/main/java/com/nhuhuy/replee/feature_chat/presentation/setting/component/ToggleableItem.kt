package com.nhuhuy.replee.feature_chat.presentation.setting.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource

@Composable
fun ToggleableItem(
    @StringRes res: Int,
    @StringRes subRes: Int,
    checked: Boolean,
    onCheckedChange: () -> Unit,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Box(
        modifier = modifier.clip(shape)
    ) {
        ListItem(
            colors = ListItemDefaults.colors(
                containerColor = containerColor,
                headlineColor = contentColor,
                supportingColor = contentColor,
                leadingIconColor = contentColor
            ),
            headlineContent = {
                Text(
                    text = stringResource(res)
                )
            },
            supportingContent = {
                Text(
                    text = stringResource(subRes)
                )
            },
            trailingContent = {
                Switch(
                    checked = checked,
                    onCheckedChange = {
                        onCheckedChange()
                    }
                )
            }
        )
    }
}