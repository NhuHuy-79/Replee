package com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.background

import androidx.lifecycle.viewModelScope
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.core.common.di.ChatScopeId
import com.nhuhuy.replee.core.common.di.ScopeHolder
import com.nhuhuy.replee.core.common.utils.ApplicationCoroutineScope
import com.nhuhuy.replee.core.domain.usecase.GetAccountByIdUseCase
import com.nhuhuy.replee.core.sync.domain.usecase.message.SyncMessageUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.MarkMessagesReadUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.metadata.UpdateReadTimeUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.metadata.UpdateTypingUseCase
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.ChatMediator
import com.nhuhuy.replee.feature_chat.utils.ChatSessionManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ChatBackgroundViewModel.Factory::class)
class ChatBackgroundViewModel @AssistedInject constructor(
    @param:ChatScopeId private val scopeId: String,
    @Assisted("otherUserId") private val otherUserId: String,
    @Assisted("currentUserId") private val currentUserId: String,
    @Assisted("messageId") private val anchorMessageId: String? = null,
    @param:ApplicationCoroutineScope private val externalScope: CoroutineScope,
    private val scopeHolder: ScopeHolder,
    private val chatSessionManager: ChatSessionManager,
    private val updateReadTimeUseCase: UpdateReadTimeUseCase,
    private val syncMessageUseCase: SyncMessageUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val markMessagesReadUseCase: MarkMessagesReadUseCase,
    private val updateTypingUseCase: UpdateTypingUseCase,
) : BaseViewModel<ChatBackgroundAction, ChatBackgroundEvent, ChatBackgroundState>() {
    private val _state = MutableStateFlow(ChatBackgroundState())
    override val state = _state.asStateFlow()

    private var currentUserReadingTime = 0L
    private val mediator by lazy {
        scopeHolder.getOrCreateMediator(scopeId = scopeId) { ChatMediator() }
    }

    init {
        mediator.initializeState(
            currentUserId = currentUserId,
            otherUserId = otherUserId,
        )
        chatSessionManager.setCurrentChatId(conversationId = mediator.currentState.conversationId)
        syncMessageUseCase(conversationId = mediator.currentState.conversationId)
            .launchIn(viewModelScope)
        viewModelScope.launch {
            updateReadingTime()
            val currentAccountDeferred = async { getAccountByIdUseCase(currentUserId) }
            val otherAccountDeferred = async { getAccountByIdUseCase(otherUserId) }
            _state.reduce {
                copy(
                    currentAccount = currentAccountDeferred.await(),
                    otherAccount = otherAccountDeferred.await()
                )
            }
        }
    }

    override fun onAction(action: ChatBackgroundAction) {

    }

    private suspend fun updateReadingTime() {
        val currentTime = System.currentTimeMillis()

        if (currentTime > currentUserReadingTime + 3000) {
            updateReadTimeUseCase(
                conversationId = mediator.currentState.conversationId,
                userId = currentUserId,
                currentTime = currentTime
            )
            currentUserReadingTime = currentTime
        }

    }

    override fun onCleared() {
        externalScope.launch {
            markMessagesReadUseCase(
                conversationId = mediator.currentState.conversationId,
                receiverId = currentUserId
            )
            updateTypingUseCase(
                conversationId = mediator.currentState.conversationId,
                userId = currentUserId,
                typing = false
            )
        }
        super.onCleared()
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("otherUserId") otherUserId: String,
            @Assisted("currentUserId") currentUserId: String,
            @Assisted("messageId") anchorMessageId: String? = null
        ): ChatBackgroundViewModel
    }
}