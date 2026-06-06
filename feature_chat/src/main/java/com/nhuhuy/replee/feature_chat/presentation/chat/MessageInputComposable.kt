package com.nhuhuy.replee.feature_chat.presentation.chat

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.core.net.toUri
import com.nhuhuy.replee.core.common.utils.showLongToast
import com.nhuhuy.replee.core.presentation.launcher.rememberCameraRequestPicker
import com.nhuhuy.replee.core.presentation.launcher.rememberImagePicker
import com.nhuhuy.replee.feature_chat.R
import com.nhuhuy.replee.feature_chat.presentation.chat.component.ReplyBanner
import com.nhuhuy.replee.feature_chat.presentation.chat.component.message.MessageInput
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.ChatMediatorState
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.background.ChatBackgroundCombineState
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.background.ChatBackgroundState
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.input.MessageInputAction
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.input.MessageInputState
import kotlinx.coroutines.delay

@Composable
fun MessageInputComposable(
    chatMediatorState: ChatMediatorState,
    chatBackgroundState: ChatBackgroundState,
    chatBackgroundCombineState: ChatBackgroundCombineState,
    messageInputState: MessageInputState,
    onAction: (MessageInputAction) -> Unit,
) {
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val onCameraClick = rememberCameraRequestPicker(
        onImageCaptured = { file ->
            onAction(MessageInputAction.OnImageSelect(file.toUri()))
        },
        onPermissionDenied = {
            context.showLongToast(R.string.permission_camera)
        }
    )
    val onImageClick = rememberImagePicker { uri ->
        uri?.let {
            onAction(MessageInputAction.OnImageSelect(uri))
        }
    }

    val replyToAccount =
        if (chatMediatorState.selectedMessage?.senderId == chatMediatorState.currentUserId)
            chatBackgroundState.otherAccount else chatBackgroundState.currentAccount

    LaunchedEffect(chatMediatorState.isReplying) {
        if (chatMediatorState.isReplying) {
            delay(100)
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    ReplyBanner(
        onCancelReply = { onAction(MessageInputAction.OnReplyRemove) },
        replyTo = replyToAccount.name,
        currentMessage = chatMediatorState.selectedMessage,
        isReplying = chatMediatorState.isReplying
    )


    if (!chatBackgroundCombineState.ownerIsBlock) {
        MessageInput(
            focusRequester = focusRequester,
            value = messageInputState.input,
            onValueChange = { value ->
                onAction(MessageInputAction.OnMessageInputChange(value))
            },
            onCameraClick = onCameraClick,
            onImageClick = onImageClick,
            onSendMessage = { onAction(MessageInputAction.OnSendButtonClick) },
            scrollCallback = { },
            modifier = Modifier.fillMaxWidth()
        )
    }
}