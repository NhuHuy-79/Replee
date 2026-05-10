package com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.main

import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.di.ChatScopeId
import com.nhuhuy.replee.core.common.di.ScopeHolder
import com.nhuhuy.replee.core.common.utils.ApplicationCoroutineScope
import com.nhuhuy.replee.feature_chat.domain.usecase.message.MarkMessagesReadUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.metadata.UpdateTypingUseCase
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.ChatMediator
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.ChatMediatorState
import com.nhuhuy.replee.feature_chat.utils.ChatSessionManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ChatViewModel.Factory::class)
class ChatViewModel @AssistedInject constructor(
    @param:ChatScopeId private val scopeId: String,
    @Assisted("otherUserId") private val otherUserId: String,
    @Assisted("currentUserId") private val currentUserId: String,
    @Assisted("messageId") private val anchorMessageId: String? = null,
    @param:ApplicationCoroutineScope private val externalScope: CoroutineScope,
    private val scopeHolder: ScopeHolder,
    private val chatSessionManager: ChatSessionManager,
    private val updateTypingUseCase: UpdateTypingUseCase,
    private val markMessagesReadUseCase: MarkMessagesReadUseCase,
) : BaseViewModel<ChatAction, ChatEvent, ChatState>() {

    private val mediator by lazy {
        scopeHolder.getOrCreateMediator(scopeId = scopeId) { ChatMediator() }
    }

    val mediatorState: StateFlow<ChatMediatorState> = mediator.state

    private val _state = MutableStateFlow(ChatState(currentUserId = currentUserId))
    override val state: StateFlow<ChatState> = _state.asStateFlow()

    init {
        mediator.initializeState(
            currentUserId = currentUserId,
            otherUserId = otherUserId,
            anchorMessageId = anchorMessageId
        )
        chatSessionManager.setCurrentChatId(conversationId = mediator.currentState.conversationId)
    }

    override fun onAction(action: ChatAction) {}

    override fun onCleared() {
        val conversationId = mediator.currentState.conversationId
        chatSessionManager.setCurrentChatId(conversationId = null)
        externalScope.launch {
            updateTypingUseCase(
                conversationId = conversationId,
                userId = currentUserId,
                typing = false
            )
            markMessagesReadUseCase(
                conversationId = conversationId,
                currentUserId = currentUserId
            )
        }

        scopeHolder.releaseScope(scopeId)
        super.onCleared()
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("otherUserId") otherUserId: String,
            @Assisted("currentUserId") currentUserId: String,
            @Assisted("messageId") anchorMessageId: String? = null
        ): ChatViewModel
    }
}
