package com.nhuhuy.replee.feature_chat.presentation.chat.component.message

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.replee.core.design_system.component.SheetContainer
import com.nhuhuy.replee.core.design_system.component.UserImage
import com.nhuhuy.replee.feature_chat.domain.model.message.Message

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageReactionBottomSheet(
    currentUser: Account,
    otherUser: Account,
    message: Message,
    onReactionDelete: (reaction: String, messageId: String) -> Unit,
    onDismiss: () -> Unit
) {
    // Tối ưu nhóm dữ liệu
    val allReactionsMap = remember(message.ownerReactions, message.otherUserReactions) {
        val map = mutableMapOf<String, MutableList<Account>>()

        message.ownerReactions.forEach { emoji ->
            map.getOrPut(emoji) { mutableListOf() }.add(currentUser)
        }

        message.otherUserReactions.forEach { emoji ->
            map.getOrPut(emoji) { mutableListOf() }.add(otherUser)
        }
        map
    }

    val reactionEmojis = allReactionsMap.keys.toList()
    val totalReactions = remember(allReactionsMap) { allReactionsMap.values.sumOf { it.size } }

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    SheetContainer(onDismiss = onDismiss) {
        if (allReactionsMap.isNotEmpty()) {

            // Material Design 3: Secondary Tab Row cho các bộ lọc phụ
            SecondaryScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 8.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            ) {
                // Tab "Tất cả"
                TabItem(
                    title = "Tất cả",
                    count = totalReactions,
                    isSelected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 }
                )

                // Các Tab Emoji
                reactionEmojis.forEachIndexed { index, emoji ->
                    TabItem(
                        title = emoji,
                        count = allReactionsMap[emoji]?.size ?: 0,
                        isSelected = selectedTabIndex == index + 1,
                        onClick = { selectedTabIndex = index + 1 }
                    )
                }
            }

            // Lọc user hiển thị theo Tab
            val usersToDisplay = remember(selectedTabIndex, allReactionsMap) {
                if (selectedTabIndex == 0) {
                    allReactionsMap.values.flatten().distinctBy { it.id }
                } else {
                    val currentEmoji = reactionEmojis[selectedTabIndex - 1]
                    allReactionsMap[currentEmoji] ?: emptyList()
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp), // Tự động co dãn, max 400dp
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(
                    items = usersToDisplay,
                    key = { it.id }
                ) { user ->
                    val isMine = user.id == currentUser.id

                    ReactionUserItem(
                        user = user,
                        isMine = isMine,
                        onRemoveClick = {
                            if (isMine) {
                                val emojiToRemove = if (selectedTabIndex == 0) {
                                    message.ownerReactions.firstOrNull() ?: ""
                                } else {
                                    reactionEmojis[selectedTabIndex - 1]
                                }

                                if (emojiToRemove.isNotEmpty()) {
                                    onReactionDelete(emojiToRemove, message.messageId)
                                    onDismiss()
                                }
                            }
                        }
                    )
                }
            }
        } else {
            // Trạng thái trống (Empty State) chuẩn MD3
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Chưa có cảm xúc nào",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TabItem(
    title: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Tab(
        selected = isSelected,
        onClick = onClick,
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = title,
                    // Emoji cần giữ kích thước tĩnh gọn gàng, text "Tất cả" dùng typo MD3
                    style = if (title == "Tất cả") MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

@Composable
fun ReactionUserItem(
    user: Account,
    isMine: Boolean,
    onRemoveClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Mở profile user (Tuỳ chọn) */ }
            .padding(horizontal = 16.dp, vertical = 10.dp), // Spacing chuẩn
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserImage(
            photoUrl = user.imageUrl,
            userName = user.name,
            modifier = Modifier.size(48.dp) // Kích thước Avatar chuẩn
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = if (isMine) "Bạn" else user.name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f) // Đẩy icon xoá sang phải
        )

        if (isMine) {
            IconButton(
                onClick = onRemoveClick,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Gỡ cảm xúc",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}