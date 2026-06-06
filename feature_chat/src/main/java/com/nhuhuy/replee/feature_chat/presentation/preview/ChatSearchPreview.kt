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
import com.nhuhuy.replee.feature_chat.presentation.search.SearchScreen
import com.nhuhuy.replee.feature_chat.presentation.search.state.SearchState
import kotlinx.coroutines.flow.MutableStateFlow

@Preview
@Composable
fun ChatSearchPreview() {
    val fakeSearchResults = listOf(
        Message(
            conversationId = "conv_1",
            messageId = "s1",
            senderId = "u1",
            receiverId = "me_123",
            content = "The design for the new feature is looking great.",
            sentAt = System.currentTimeMillis() - 1000 * 60 * 60 * 5,
            status = MessageStatus.SYNCED,
            type = MessageType.TEXT
        ),
        Message(
            conversationId = "conv_1",
            messageId = "s2",
            senderId = "me_123",
            receiverId = "u1",
            content = "I agree, but we might need to tweak the design slightly.",
            sentAt = System.currentTimeMillis() - 1000 * 60 * 60 * 4,
            status = MessageStatus.SYNCED,
            type = MessageType.TEXT
        ),
        Message(
            conversationId = "conv_1",
            messageId = "s3",
            senderId = "u1",
            receiverId = "me_123",
            content = "Let's discuss the design during our next meeting.",
            sentAt = System.currentTimeMillis() - 1000 * 60 * 60 * 3,
            status = MessageStatus.SYNCED,
            type = MessageType.TEXT
        )
    )

    val searchResults =
        MutableStateFlow(PagingData.from(fakeSearchResults)).collectAsLazyPagingItems()

    RepleeTheme(darkTheme = true) {
        PreviewFrame(title = "Search Results") {
            SearchScreen(
                state = SearchState(
                    searchQuery = "design",
                    currentUser = Account(id = "me_123", name = "Nhu Huy"),
                    otherUser = Account(
                        id = "u1",
                        name = "Alex Johnson",
                        imageUrl = "https://images.unsplash.com/photo-1599566150163-29194dcaad36?auto=format&fit=crop&w=250&q=80"
                    )
                ),
                searchResults = searchResults,
                onAction = {}
            )
        }
    }
}
