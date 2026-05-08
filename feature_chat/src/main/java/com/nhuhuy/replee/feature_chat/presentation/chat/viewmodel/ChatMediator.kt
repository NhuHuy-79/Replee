package com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel

import androidx.compose.runtime.Immutable
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.core.common.utils.ChatIdGenerator
import com.nhuhuy.replee.core.model.chat.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Immutable
data class ChatMediatorState(
    val selectedMessage: Message? = null,
    val isReplying: Boolean = false,
    val currentUserId: String = "",
    val otherUserId: String = "",
) {
    val conversationId: String
        get() = ChatIdGenerator.generate(
            uid1 = currentUserId,
            uid2 = otherUserId
        )
}

class ChatMediator {
    private val _state = MutableStateFlow(ChatMediatorState())
    val state = _state.asStateFlow()

    val currentState get() = state.value

    fun initializeState(
        currentUserId: String,
        otherUserId: String,
    ) {
        _state.reduce {
            copy(currentUserId = currentUserId, otherUserId = otherUserId)
        }
    }

    fun removeSelectedMessage() {
        _state.reduce {
            copy(selectedMessage = null, isReplying = false)
        }
    }
}