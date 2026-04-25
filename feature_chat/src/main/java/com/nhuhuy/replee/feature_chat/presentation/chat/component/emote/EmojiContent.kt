package com.nhuhuy.replee.feature_chat.presentation.chat.component.emote

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.emoji2.emojipicker.EmojiPickerView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenEmojiPickerDialog(
    onDismiss: () -> Unit,
    onEmojiSelected: (String) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.surface,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Chọn Biểu Tượng",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Đóng màn hình"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        // Đồng bộ màu nền của thanh TopBar với màn hình
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        ) { paddingValues ->
            SystemEmojiPicker(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.surface),
                onEmojiSelected = { emoji ->
                    onEmojiSelected(emoji)
                    onDismiss()
                }
            )
        }
    }
}

// Hàm SystemEmojiPicker của bạn đã được chỉnh sửa nhẹ
@Composable
fun SystemEmojiPicker(
    modifier: Modifier = Modifier,
    onEmojiSelected: (String) -> Unit
) {
    AndroidView(
        factory = { context ->
            EmojiPickerView(context).apply {
                // Background của thằng AndroidView tự động trong suốt
                // nên nó sẽ ăn theo màu Surface của Compose ở ngoài
                setOnEmojiPickedListener { emojiViewItem ->
                    onEmojiSelected(emojiViewItem.emoji)
                }
            }
        },
        // Bỏ height(300.dp) đi để nhường quyền quyết định cho thẻ cha (modifier)
        modifier = modifier.fillMaxWidth()
    )
}