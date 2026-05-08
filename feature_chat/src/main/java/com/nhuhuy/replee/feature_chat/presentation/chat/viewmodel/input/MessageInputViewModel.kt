package com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.input

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.core.common.di.ScopeHolder
import com.nhuhuy.replee.core.model.validate.ValidateFileResult
import com.nhuhuy.replee.feature_chat.di.ChatScopeId
import com.nhuhuy.replee.feature_chat.domain.usecase.file.SendFileMessageUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.file.ValidateFileSizeUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.SendMessageUseCase
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.ChatMediator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MessageInputViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val sendFileMessageUseCase: SendFileMessageUseCase,
    private val validateFileSizeUseCase: ValidateFileSizeUseCase,
    private val scopeHolder: ScopeHolder,
    @field:ChatScopeId private val chatScopeId: String
) : BaseViewModel<MessageInputAction, MessageInputEvent, MessageInputState>() {
    private val chatMediator by lazy {
        scopeHolder.getOrCreateMediator(scopeId = chatScopeId) { ChatMediator() }
    }
    private val _state = MutableStateFlow(MessageInputState())
    override val state: StateFlow<MessageInputState>
        get() = _state.asStateFlow()
    private val currentState: MessageInputState get() = state.value

    override fun onAction(action: MessageInputAction) {
        when (action) {
            is MessageInputAction.OnMessageInputChange -> _state.reduce { copy(input = action.text) }
            is MessageInputAction.OnImageSelect -> sendFileAsMessage(action.uri)
            MessageInputAction.OnReplyRemove -> chatMediator.removeSelectedMessage()
            MessageInputAction.OnSendButtonClick -> sendTextMessage()
        }
    }

    private fun sendFileAsMessage(uri: Uri) {
        viewModelScope.launch {
            val repliedMessage = chatMediator.currentState.selectedMessage
            val validateFileResult =
                validateFileSizeUseCase(uriPath = uri.toString())

            when (validateFileResult) {
                is ValidateFileResult.FileTooLarge -> onEvent(MessageInputEvent.FileValidateError.FileTooLarge)
                is ValidateFileResult.UnSupported -> onEvent(MessageInputEvent.FileValidateError.Unsupported)
                is ValidateFileResult.Valid -> {
                    chatMediator.removeSelectedMessage()
                    sendFileMessageUseCase(
                        senderId = chatMediator.currentState.currentUserId,
                        receiverId = chatMediator.currentState.otherUserId,
                        uriPath = uri.toString(),
                        conversationId = chatMediator.currentState.conversationId,
                        repliedMessage = repliedMessage
                    )
                }

                else -> onEvent(MessageInputEvent.FileValidateError.Unknown)
            }
        }
    }

    private fun sendTextMessage() {
        viewModelScope.launch {
            val messageInput: String = currentState.input
            val currentMessage = chatMediator.currentState.selectedMessage
            chatMediator.removeSelectedMessage()
            _state.reduce { copy(input = "") }
            sendMessageUseCase(
                repliedMessage = currentMessage,
                senderId = chatMediator.currentState.currentUserId,
                receiverId = chatMediator.currentState.otherUserId,
                conversationId = chatMediator.currentState.conversationId,
                text = messageInput
            )
        }
    }

}
