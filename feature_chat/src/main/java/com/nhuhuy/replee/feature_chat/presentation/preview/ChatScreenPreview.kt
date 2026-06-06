package com.nhuhuy.replee.feature_chat.presentation.preview

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers.YELLOW_DOMINATED_EXAMPLE
import com.nhuhuy.replee.core.design_system.component.PreviewFrame
import com.nhuhuy.replee.core.design_system.theme.RepleeTheme
import com.nhuhuy.replee.core.model.account.Account
import com.nhuhuy.replee.core.model.chat.LocalPathMessage
import com.nhuhuy.replee.core.model.chat.Message
import com.nhuhuy.replee.core.model.chat.MessageStatus
import com.nhuhuy.replee.core.model.chat.MessageType
import com.nhuhuy.replee.feature_chat.presentation.chat.ChatScreen
import com.nhuhuy.replee.feature_chat.presentation.chat.model.MessagePosition
import com.nhuhuy.replee.feature_chat.presentation.chat.model.MessageUiModel
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.ChatMediatorState
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.background.ChatBackgroundCombineState
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.background.ChatBackgroundState
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.content.MessageContentState
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.input.MessageInputState

@Preview(
    wallpaper = YELLOW_DOMINATED_EXAMPLE
)
@Composable
fun ChatScreenPreview() {
    RepleeTheme(
        darkTheme = true,
        dynamicColor = true
    ) {
        PreviewFrame(
            title = "Visual Conversation",
            backgroundColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            ChatScreen(
                messages = fakeMessages,
                messageContentState = MessageContentState(),
                chatBackgroundState = fakeChatBackgroundState,
                chatBackgroundCombineState = fakeChatBackgroundCombineState,
                chatMediatorState = ChatMediatorState(
                    currentUserId = "me_123",
                    otherUserId = "u1"
                ),
                messageInputState = MessageInputState(input = "That sounds great!"),
                onInputAction = {},
                onBackgroundAction = {},
                onMessageAction = {}
            )
        }
    }
}

val fakeChatBackgroundState = ChatBackgroundState(
    currentAccount = Account(
        id = "me_123",
        name = "Nhu Huy",
        imageUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=250&q=80"
    ),
    otherAccount = Account(
        id = "u1",
        name = "Alex Johnson",
        imageUrl = "https://images.unsplash.com/photo-1599566150163-29194dcaad36?auto=format&fit=crop&w=250&q=80",
        online = true
    )
)

val fakeChatBackgroundCombineState = ChatBackgroundCombineState(
    typingUserIds = listOf("u1")
)

val fakeMessages = listOf(
    MessageUiModel.DateSeparator("May 8, 2024"),
    MessageUiModel.MessageItem(
        data = LocalPathMessage(
            message = Message(
                conversationId = "conv_1",
                messageId = "old_1",
                senderId = "me_123",
                receiverId = "u1",
                content = "Hey! Have you seen the latest project requirements? I think we need to adjust the database schema slightly.",
                sentAt = System.currentTimeMillis() - 1000 * 60 * 60 * 48,
                status = MessageStatus.SYNCED,
                type = MessageType.TEXT
            ),
            localPath = null
        ),
        position = MessagePosition.SINGLE,
        isLastInGroup = true
    ),
    MessageUiModel.DateSeparator("Yesterday"),
    /* MessageUiModel.MessageItem(
         data = LocalPathMessage(
             message = Message(
                 conversationId = "conv_1",
                 messageId = "m1",
                 senderId = "me_123",
                 receiverId = "u1",
                 content = "Yeah, I'll take a look at that today. By the way, the UI refactor is almost done!",
                 sentAt = System.currentTimeMillis() - 1000 * 60 * 60 * 24,
                 status = MessageStatus.SEEN,
                 type = MessageType.TEXT
             ),
             localPath = null
         ),
         position = MessagePosition.SINGLE,
         isLastInGroup = true
     ),*/
    /* MessageUiModel.DateSeparator("Today"),*/
    /* MessageUiModel.MessageItem(
         data = LocalPathMessage(
             message = Message(
                 conversationId = "conv_1",
                 messageId = "m2",
                 senderId = "u1",
                 receiverId = "me_123",
                 content = "Awesome! Can't wait to see it.",
                 sentAt = System.currentTimeMillis() - 1000 * 60 * 60 * 2,
                 status = MessageStatus.SYNCED,
                 type = MessageType.TEXT
             ),
             localPath = null
         ),
         position = MessagePosition.START,
         isLastInGroup = false
     ),*/
    /* MessageUiModel.MessageItem(
         data = LocalPathMessage(
             message = Message(
                 conversationId = "conv_1",
                 messageId = "m3",
                 senderId = "u1",
                 receiverId = "me_123",
                 content = "I've sent some inspiration images for the dark mode theme.",
                 sentAt = System.currentTimeMillis() - 1000 * 60 * 60 * 2 + 1000 * 10,
                 status = MessageStatus.SYNCED,
                 type = MessageType.TEXT
             ),
             localPath = null
         ),
         position = MessagePosition.MIDDLE,
         isLastInGroup = false
     ),*/
    MessageUiModel.MessageItem(
        data = LocalPathMessage(
            message = Message(
                conversationId = "conv_1",
                messageId = "m4",
                senderId = "u1",
                receiverId = "me_123",
                content = "Let me know which one you prefer.",
                sentAt = System.currentTimeMillis() - 1000 * 60 * 60 * 2 + 1000 * 20,
                status = MessageStatus.SYNCED,
                type = MessageType.TEXT,
                otherUserReactions = listOf("❤️", "🔥")
            ),
            localPath = null
        ),
        position = MessagePosition.END,
        isLastInGroup = true
    ),
    MessageUiModel.MessageItem(
        data = LocalPathMessage(
            message = Message(
                conversationId = "conv_1",
                messageId = "img_1",
                senderId = "u1",
                receiverId = "me_123",
                content = "This is my workspace setup.",
                sentAt = System.currentTimeMillis() - 1000 * 60 * 50,
                status = MessageStatus.SYNCED,
                type = MessageType.TEXT,
            ),
            localPath = null
        ),
        position = MessagePosition.START,
        isLastInGroup = true
    ),
    MessageUiModel.MessageItem(
        data = LocalPathMessage(
            message = Message(
                conversationId = "conv_1",
                messageId = "m5",
                senderId = "me_123",
                receiverId = "u1",
                content = "Looks very professional! I love the minimal aesthetic.",
                sentAt = System.currentTimeMillis() - 1000 * 60 * 45,
                status = MessageStatus.SEEN,
                type = MessageType.TEXT,
                repliedMessageId = "img_1",
                repliedMessageContent = "This is my workspace setup.",
                repliedMessageSenderId = "u1",
                repliedMessageType = MessageType.IMAGE,
                repliedMessageRemoteUrl = "https://images.unsplash.com/photo-1497215728101-856f4ea42174?auto=format&fit=crop&w=500&q=80",
                otherUserReactions = listOf("✨")
            ),
            localPath = null
        ),
        position = MessagePosition.SINGLE,
        isLastInGroup = true
    ),
    MessageUiModel.MessageItem(
        data = LocalPathMessage(
            message = Message(
                conversationId = "conv_1",
                messageId = "m6",
                senderId = "me_123",
                receiverId = "u1",
                content = "I think the first one is the most balanced.",
                sentAt = System.currentTimeMillis() - 1000 * 60 * 40,
                status = MessageStatus.SYNCED,
                type = MessageType.TEXT,
                repliedMessageId = "m4",
                repliedMessageContent = "Let me know which one you prefer.",
                repliedMessageSenderId = "u1",
                repliedMessageType = MessageType.TEXT,
                ownerReactions = listOf("👍")
            ),
            localPath = null
        ),
        position = MessagePosition.SINGLE,
        isLastInGroup = true
    ),
    MessageUiModel.MessageItem(
        data = LocalPathMessage(
            message = Message(
                conversationId = "conv_1",
                messageId = "m7",
                senderId = "me_123",
                receiverId = "u1",
                content = "Wait, I just found a tiny issue with the icon alignment.",
                sentAt = System.currentTimeMillis() - 1000 * 60 * 10,
                status = MessageStatus.SYNCED,
                type = MessageType.TEXT
            ),
            localPath = null
        ),
        position = MessagePosition.START,
        isLastInGroup = false
    ),
    MessageUiModel.MessageItem(
        data = LocalPathMessage(
            message = Message(
                conversationId = "conv_1",
                messageId = "m8",
                senderId = "me_123",
                receiverId = "u1",
                content = "I'll fix it in 5 mins and push the code.",
                sentAt = System.currentTimeMillis() - 1000 * 60 * 9,
                status = MessageStatus.SYNCED,
                type = MessageStatus.SYNCED.name.let { MessageType.TEXT }
            ),
            localPath = null
        ),
        position = MessagePosition.END,
        isLastInGroup = true
    ),
    MessageUiModel.MessageItem(
        data = LocalPathMessage(
            message = Message(
                conversationId = "conv_1",
                messageId = "m9",
                senderId = "me_123",
                receiverId = "u1",
                content = "Can you also double check the SVG exports?",
                sentAt = System.currentTimeMillis() - 1000 * 60 * 5,
                status = MessageStatus.FAILED,
                type = MessageType.TEXT
            ),
            localPath = null
        ),
        position = MessagePosition.SINGLE,
        isLastInGroup = true
    ),
    MessageUiModel.MessageItem(
        data = LocalPathMessage(
            message = Message(
                conversationId = "conv_1",
                messageId = "m10",
                senderId = "me_123",
                receiverId = "u1",
                content = "Retrying the export check now...",
                sentAt = System.currentTimeMillis(),
                status = MessageStatus.PENDING,
                type = MessageType.TEXT
            ),
            localPath = null
        ),
        position = MessagePosition.SINGLE,
        isLastInGroup = true
    )
)
