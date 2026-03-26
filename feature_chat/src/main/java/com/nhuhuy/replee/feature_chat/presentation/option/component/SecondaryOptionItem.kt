package com.nhuhuy.replee.feature_chat.presentation.option.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.feature_chat.R

@Composable
fun SecondaryOptionItem(
    @StringRes res: Int,
    @StringRes sub: Int,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer
) {
    Box(
        modifier = modifier.clip(shape)
    ) {
        ListItem(
            colors = ListItemDefaults.colors(
                containerColor = containerColor,
                headlineColor = contentColor,
                leadingIconColor = contentColor
            ),
            headlineContent = {
                Text(
                    text = stringResource(res),
                    color = contentColor
                )
            },
            supportingContent = {
                Text(
                    text = stringResource(sub),
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            leadingContent = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor
                )
            }
        )
    }
}

enum class SecondaryOption(
    @StringRes val label: Int,
    @StringRes val content: Int,
    val icon: ImageVector
) {
    SET_NICK(
        R.string.setting_set_nickname,
        R.string.setting_set_nickname_sub,
        Icons.Rounded.AccountCircle
    ),
    BLOCK(R.string.setting_block, R.string.setting_block_sub, Icons.Rounded.Block),
    SET_THEME(
        R.string.setting_choose_theme,
        R.string.setting_choose_theme_sub,
        Icons.Rounded.ColorLens
    ),
    DELETE_CONVERSATION(
        R.string.setting_delete_chat,
        R.string.setting_delete_chat_sub,
        Icons.Rounded.Delete
    )
}

