package com.nhuhuy.replee.feature_chat.navigation

import android.content.ClipData
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nhuhuy.replee.core.presentation.ObserveEffect
import com.nhuhuy.replee.core.presentation.component.BoxContainer
import com.nhuhuy.replee.feature_chat.presentation.chat.ChatScreen
import com.nhuhuy.replee.feature_chat.presentation.chat.component.dialog.FullImageDialog
import com.nhuhuy.replee.feature_chat.presentation.chat.component.emote.FullScreenEmojiPickerDialog
import com.nhuhuy.replee.feature_chat.presentation.chat.component.message.MessageSheet
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.background.ChatBackgroundEvent
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.background.ChatBackgroundViewModel
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.content.MessageContentAction
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.content.MessageContentViewModel
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.input.MessageInputViewModel
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.main.ChatOverlay
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.main.ChatViewModel
import kotlinx.coroutines.launch

@Composable
fun ChatRoute(
    chatViewModel: ChatViewModel,
    chatBackgroundViewModel: ChatBackgroundViewModel,
    messageContentViewModel: MessageContentViewModel,
    messageInputViewModel: MessageInputViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSearch: (conversationId: String, otherUserId: String, currentUserId: String) -> Unit,
    onNavigateToPin: (conversationId: String, otherUserId: String, currentUserId: String) -> Unit,
    onNavigateToInformation: (
        currentUserId: String,
        conversationId: String,
        otherUserId: String,
    ) -> Unit
) = BoxContainer {
    val backgroundState by chatBackgroundViewModel.state.collectAsStateWithLifecycle()
    val combineState by chatBackgroundViewModel.combineState.collectAsStateWithLifecycle()
    val contentState by messageContentViewModel.state.collectAsStateWithLifecycle()
    val messages by messageContentViewModel.messagesUiFlow.collectAsStateWithLifecycle()
    val inputState by messageInputViewModel.state.collectAsStateWithLifecycle()
    val mediatorState by chatViewModel.mediatorState.collectAsStateWithLifecycle()
    
    val coroutineScope = rememberCoroutineScope()
    val localClipboard = LocalClipboard.current

    ObserveEffect(chatBackgroundViewModel.event) { event ->
        when (event) {
            ChatBackgroundEvent.NavigateBack -> onNavigateBack()
            is ChatBackgroundEvent.NavigateToSearch -> onNavigateToSearch(
                event.conversationId,
                event.otherUserId,
                event.currentUserId
            )

            is ChatBackgroundEvent.NavigateToPin -> onNavigateToPin(
                event.conversationId,
                event.otherUserId,
                event.currentUserId
            )

            is ChatBackgroundEvent.NavigateToInformation -> onNavigateToInformation(
                event.currentUserId,
                event.conversationId,
                event.otherUserId,
            )
        }
    }

    ChatScreen(
        messageContentState = contentState,
        chatBackgroundState = backgroundState,
        chatBackgroundCombineState = combineState,
        chatMediatorState = mediatorState,
        messageInputState = inputState,
        messages = messages,
        onInputAction = messageInputViewModel::onAction,
        onBackgroundAction = chatBackgroundViewModel::onAction,
        onMessageAction = messageContentViewModel::onAction
    )

    when (val overlay = contentState.overlay) {
        is ChatOverlay.FullImage -> {
            FullImageDialog(
                url = overlay.url,
                onDismiss = { messageContentViewModel.onAction(MessageContentAction.OnDismiss) }
            )
        }

        ChatOverlay.None -> Unit
        is ChatOverlay.MessageOption -> {
            MessageSheet(
                currentUserId = backgroundState.currentAccount.id,
                message = overlay.message,
                onDismiss = { messageContentViewModel.onAction(MessageContentAction.OnDismiss) },
                onMessagePin = { messageContentViewModel.onAction(MessageContentAction.OnMessageContentPin) },
                onMessageUnPin = { messageContentViewModel.onAction(MessageContentAction.OnMessageContentUnPin) },
                onMessageReply = { messageContentViewModel.onAction(MessageContentAction.OnMessageContentReply) },
                onMessageDelete = { messageContentViewModel.onAction(MessageContentAction.OnMessageContentDelete) },
                onReactionSelect = { reaction ->
                    messageContentViewModel.onAction(
                        MessageContentAction.OnReactionSelect(
                            reaction
                        )
                    )
                },
                onReactionMoreClick = { messageContentViewModel.onAction(MessageContentAction.OnReactionMoreClick) },
                onMessageCopy = {
                    coroutineScope.launch {
                        val clipData = ClipData.newPlainText("plain text", overlay.message.content)
                        val clipEntry = ClipEntry(clipData)
                        localClipboard.setClipEntry(clipEntry)
                        messageContentViewModel.onAction(MessageContentAction.OnDismiss)
                    }
                }
            )
        }

        ChatOverlay.EmojiPicker -> {
            FullScreenEmojiPickerDialog(
                onDismiss = { messageContentViewModel.onAction(MessageContentAction.OnDismiss) },
                onEmojiSelected = { reaction ->
                    messageContentViewModel.onAction(
                        MessageContentAction.OnReactionSelect(
                            reaction
                        )
                    )
                }
            )
        }

        is ChatOverlay.MessageReaction -> {
            // Handle if needed
        }
    }
}
