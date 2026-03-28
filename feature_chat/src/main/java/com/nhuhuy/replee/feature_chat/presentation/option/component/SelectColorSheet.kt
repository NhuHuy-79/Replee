package com.nhuhuy.replee.feature_chat.presentation.option.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.core.data.data_store.ChatColor
import com.nhuhuy.replee.core.design_system.component.SheetContainer
import com.nhuhuy.replee.feature_chat.utils.toPrimaryColor


@Composable
fun SelectColorSheet(
    onDismiss: () -> Unit = {},
    currentColor: ChatColor,
    onColorSelected: (ChatColor) -> Unit = {},
    colorList: List<ChatColor> = ChatColor.entries,
) {
    SheetContainer(
        onDismiss = onDismiss
    ) {
        Icon(
            imageVector = Icons.Rounded.Palette,
            contentDescription = null,
        )

        Spacer(Modifier.height(16.dp))

        LazyRow(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(colorList) { color ->
                ColorItem(
                    primaryColor = color.toPrimaryColor(),
                    selected = color == currentColor,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            onColorSelected(color)
                        }
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}


@Composable
fun ColorItem(
    modifier: Modifier = Modifier,
    selected: Boolean = true,
    primaryColor: Color,
) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.outline

    Box(
        modifier = modifier
            .size(36.dp)
            .background(
                color = primaryColor,
            )
            .border(
                width = 2.dp,
                color = borderColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
