package com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.main

import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.di.ChatScopeId
import com.nhuhuy.replee.core.common.di.ScopeHolder
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatAction
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatEvent
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatState
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.ChatMediator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel(assistedFactory = ChatViewModel.Factory::class)
class ChatViewModel @AssistedInject constructor(
    @param:ChatScopeId private val scopeId: String,
    @Assisted("otherUserId") private val otherUserId: String,
    @Assisted("currentUserId") private val currentUserId: String,
    @Assisted("messageId") private val anchorMessageId: String? = null,
    private val scopeHolder: ScopeHolder,
) : BaseViewModel<ChatAction, ChatEvent, ChatState>() {

    private val mediator by lazy {
        scopeHolder.getOrCreateMediator(scopeId = scopeId) { ChatMediator() }
    }

    private val _state = MutableStateFlow(ChatState(currentUserId = currentUserId))
    override val state: StateFlow<ChatState> = _state.asStateFlow()

    init {
        mediator.initializeState(
            currentUserId = currentUserId,
            otherUserId = otherUserId,
            anchorMessageId = anchorMessageId
        )
    }

    override fun onAction(action: ChatAction) {
        // ChatViewModel sẽ chủ yếu điều phối hoặc xử lý các action chung nếu cần
    }

    override fun onCleared() {
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
