package com.nhuhuy.replee.feature_home.presentation.preview

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers.RED_DOMINATED_EXAMPLE
import coil3.annotation.ExperimentalCoilApi
import com.nhuhuy.replee.core.common.base.ScreenState
import com.nhuhuy.replee.core.design_system.component.PreviewFrame
import com.nhuhuy.replee.core.design_system.theme.RepleeTheme
import com.nhuhuy.replee.core.model.account.Account
import com.nhuhuy.replee.core.model.chat.Conversation
import com.nhuhuy.replee.core.model.chat.MessageType
import com.nhuhuy.replee.feature_home.presentation.HomeScreen
import com.nhuhuy.replee.feature_home.presentation.state.HomeState

@OptIn(ExperimentalCoilApi::class)
@Preview(
    wallpaper = RED_DOMINATED_EXAMPLE
)
@Composable
fun HomeScreenPreview(

) {
    RepleeTheme(
        darkTheme = true,
        dynamicColor = true
    ) {
        PreviewFrame(
            title = "Simple Conversation",
            backgroundColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            HomeScreen(
                conversationListState = ScreenState.Success(conversationList),
                state = fakeHomeUiState,
                searchHistory = emptyList(),
                onAction = {}
            )
        }
    }
}


val fakeHomeUiState = HomeState(
    currentUser = Account(
        id = "me_123",
        name = "Nhu Huy",
        imageUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=250&q=80",
        online = true
    )
)

val conversationList: List<Conversation> = listOf(
    Conversation(
        id = "conv_1",
        ownerUserId = "me_123",
        otherUserId = "u1",
        otherUserName = "Alex Johnson",
        otherUserImg = "https://images.unsplash.com/photo-1599566150163-29194dcaad36?auto=format&fit=crop&w=250&q=80",
        lastMessageId = "m1",
        lastMessageContent = "Let's catch up later! Are you free at 7?",
        lastMessageTime = System.currentTimeMillis() - 1000 * 60 * 5, // 5 mins ago
        unreadMessageCount = 3,
        otherUserOnline = true
    ),
    Conversation(
        id = "conv_2",
        ownerUserId = "me_123",
        otherUserId = "u2",
        otherUserName = "Sarah Miller",
        otherUserImg = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=250&q=80",
        lastMessageId = "m2",
        lastMessageContent = "Sent an image",
        lastMessageType = MessageType.IMAGE,
        lastMessageTime = System.currentTimeMillis() - 1000 * 60 * 15, // 15 mins ago
        otherUserOnline = true
    ),
    Conversation(
        id = "conv_4",
        ownerUserId = "me_123",
        otherUserId = "u4",
        otherUserName = "John Doe",
        otherUserImg = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=250&q=80",
        lastMessageId = "m4",
        lastMessageContent = "Check out this new project I'm working on.",
        lastMessageTime = System.currentTimeMillis() - 1000 * 60 * 60 * 3, // 3 hours ago
        pinned = true,
        otherUserOnline = false
    ),
    Conversation(
        id = "conv_5",
        ownerUserId = "me_123",
        otherUserId = "u5",
        otherUserName = "Emma Wilson",
        otherUserImg = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&w=250&q=80",
        lastMessageId = "m5",
        lastMessageContent = "Happy Birthday! Hope you have a great day! 🎂",
        lastMessageTime = System.currentTimeMillis() - 1000 * 60 * 60 * 24, // 1 day ago
    ),
    Conversation(
        id = "conv_6",
        ownerUserId = "me_123",
        otherUserId = "u6",
        otherUserName = "Tech Support",
        otherUserImg = "https://images.unsplash.com/photo-1573497019940-1c28c88b4f3e?auto=format&fit=crop&w=250&q=80",
        lastMessageId = "m6",
        lastMessageContent = "Your issue ticket #882 has been resolved successfully.",
        lastMessageTime = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 2, // 2 days ago
    ),
    Conversation(
        id = "conv_7",
        ownerUserId = "me_123",
        otherUserId = "u7",
        otherUserName = "David Smith",
        otherUserImg = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=250&q=80",
        lastMessageId = "m7",
        lastMessageContent = "See you later at the gym!",
        lastMessageTime = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 3, // 3 days ago
    ),
    Conversation(
        id = "conv_8",
        ownerUserId = "me_123",
        otherUserId = "u8",
        otherUserName = "Lily Evans",
        otherUserImg = "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=250&q=80",
        lastMessageId = "m8",
        lastMessageContent = "Thanks for the notes, they were really helpful.",
        lastMessageTime = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 7, // 1 week ago
    )
)
