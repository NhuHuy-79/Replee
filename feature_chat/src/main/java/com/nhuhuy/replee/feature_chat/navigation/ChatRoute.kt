package com.nhuhuy.replee.feature_chat.navigation

import android.content.ClipData
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.nhuhuy.replee.core.design_system.ObserveEffect
import com.nhuhuy.replee.core.design_system.component.BoxContainer
import com.nhuhuy.replee.feature_chat.presentation.chat.ChatScreen
import com.nhuhuy.replee.feature_chat.presentation.chat.ChatViewModel
import com.nhuhuy.replee.feature_chat.presentation.chat.component.dialog.FullImageDialog
import com.nhuhuy.replee.feature_chat.presentation.chat.component.emote.FullScreenEmojiPickerDialog
import com.nhuhuy.replee.feature_chat.presentation.chat.component.message.MessageSheet
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatAction
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatAction.OnReactionSelect
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatEvent
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatOverlay
import kotlinx.coroutines.launch

@Composable
fun ChatRoute(
    viewModel: ChatViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSearch: (conversationId: String, otherUserId: String) -> Unit,
    onNavigateToPin: (conversationId: String, otherUserId: String) -> Unit,
    onNavigateToInformation: (
        otherUserImg: String,
        currentUserId: String,
        conversationId: String,
        otherUserId: String,
        otherUserName: String,
        otherUserEmail: String
    ) -> Unit
) = BoxContainer {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val otherUserReadTime by viewModel.otherLastReadingTime.collectAsStateWithLifecycle()
    val typingUsers by viewModel.typingUserIds.collectAsStateWithLifecycle()
    val blocked by viewModel.blocked.collectAsStateWithLifecycle()
    val pagedMessages = viewModel.pagedMessages.collectAsLazyPagingItems()

    val onAction = viewModel::onAction
    val coroutineScope = rememberCoroutineScope()
    val localClipboard = LocalClipboard.current

    ObserveEffect(viewModel.event) { event ->
        when (event) {
            ChatEvent.NavigateBack -> onNavigateBack()
            is ChatEvent.NavigateToSearch -> onNavigateToSearch(
                event.conversationId,
                event.otherUserId
            )

            is ChatEvent.NavigateToPin -> onNavigateToPin(event.conversationId, event.otherUserId)
            is ChatEvent.NavigateToInformation -> onNavigateToInformation(
                event.otherUserImg,
                event.currentUserId,
                event.conversationId,
                event.otherUserId,
                event.otherUserName,
                event.otherUserEmail
            )

            ChatEvent.FileTooLarge -> {
                // Handle
            }

            ChatEvent.UnSupportedFile -> {
                // Handle
            }

            ChatEvent.Unknown -> {
                // Handle
            }

            is ChatEvent.ScrollToAnchor -> {
                // Handled in Screen usually
            }

            is ChatEvent.SendImage.Failure -> {}
            ChatEvent.SendImage.Success -> {}
        }
    }

    ChatScreen(
        otherUserReadTime = otherUserReadTime,
        typingUsers = typingUsers,
        pagedMessages = pagedMessages,
        blocked = blocked,
        state = state,
        onAction = viewModel::onAction
    )

    when (val overlay = state.overlay) {
        is ChatOverlay.FullImage -> {
            FullImageDialog(
                url = overlay.url,
                onDismiss = { onAction(ChatAction.OnDismiss) }
            )
        }

        ChatOverlay.None -> Unit
        is ChatOverlay.MessageOption -> {
            MessageSheet(
                currentUserId = state.currentUserId,
                message = overlay.message,
                onDismiss = { onAction(ChatAction.OnDismiss) },
                onMessagePin = { onAction(ChatAction.OnMessagePin) },
                onMessageUnPin = { onAction(ChatAction.OnMessageUnPin) },
                onMessageReply = { onAction(ChatAction.OnMessageReply) },
                onMessageDelete = { onAction(ChatAction.OnMessageDelete) },
                onReactionSelect = { reaction -> onAction(OnReactionSelect(reaction)) },
                onReactionMoreClick = { onAction(ChatAction.OnReactionMoreClick) },
                onMessageCopy = {
                    coroutineScope.launch {
                        val clipData = ClipData.newPlainText(
                            "plain text",
                            state.currentMessage?.content.orEmpty()
                        )
                        val clipEntry = ClipEntry(clipData)
                        localClipboard.setClipEntry(clipEntry)
                        onAction(ChatAction.OnDismiss)
                    }
                }
            )
        }

        ChatOverlay.EmojiPicker -> {
            FullScreenEmojiPickerDialog(
                onDismiss = { onAction(ChatAction.OnDismiss) },
                onEmojiSelected = { reaction -> onAction(OnReactionSelect(reaction)) }
            )
        }

        is ChatOverlay.MessageReaction -> {

        }
    }
}
