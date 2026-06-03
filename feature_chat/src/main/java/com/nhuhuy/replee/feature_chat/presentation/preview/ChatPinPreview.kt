package com.nhuhuy.replee.feature_chat.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.nhuhuy.replee.core.design_system.component.PreviewFrame
import com.nhuhuy.replee.core.design_system.theme.RepleeTheme
import com.nhuhuy.replee.core.model.account.Account
import com.nhuhuy.replee.core.model.chat.Message
import com.nhuhuy.replee.core.model.chat.MessageStatus
import com.nhuhuy.replee.core.model.chat.MessageType
import com.nhuhuy.replee.feature_chat.presentation.pin.PinState
import com.nhuhuy.replee.feature_chat.presentation.pin.PinnedMessagesScreen
import kotlinx.coroutines.flow.MutableStateFlow

@Preview
@Composable
fun ChatPinPreview() {
    val fakePinnedMessages = listOf(
        Message(
            conversationId = "conv_1",
            messageId = "m1",
            senderId = "u1",
            receiverId = "me_123",
            content = "This is a pinned message with some very important information that we should not forget.",
            sentAt = System.currentTimeMillis() - 1000 * 60 * 60 * 24,
            status = MessageStatus.SYNCED,
            pinned = true,
            type = MessageType.TEXT
        ),
        Message(
            conversationId = "conv_1",
            messageId = "m2",
            senderId = "me_123",
            receiverId = "u1",
            content = "I've pinned the link to the shared folder here.",
            sentAt = System.currentTimeMillis() - 1000 * 60 * 60 * 2,
            status = MessageStatus.SYNCED,
            pinned = true,
            type = MessageType.TEXT
        ),
        Message(
            conversationId = "conv_1",
            messageId = "m3",
            senderId = "u1",
            receiverId = "me_123",
            content = "Don't forget the deadline is this Friday!",
            sentAt = System.currentTimeMillis() - 1000 * 60 * 30,
            status = MessageStatus.SYNCED,
            pinned = true,
            type = MessageType.TEXT
        )
    )

    val pinnedMessages =
        MutableStateFlow(PagingData.from(fakePinnedMessages)).collectAsLazyPagingItems()

    RepleeTheme(darkTheme = true) {
        PreviewFrame(title = "Pinned Messages") {
            PinnedMessagesScreen(
                state = PinState(
                    currentUser = Account(id = "me_123", name = "Nhu Huy"),
                    otherUser = Account(
                        id = "u1",
                        name = "Alex Johnson",
                        imageUrl = "https://images.unsplash.com/photo-1599566150163-29194dcaad36?auto=format&fit=crop&w=250&q=80"
                    )
                ),
                pinnedMessages = pinnedMessages,
                onAction = {}
            )
        }
    }
}
