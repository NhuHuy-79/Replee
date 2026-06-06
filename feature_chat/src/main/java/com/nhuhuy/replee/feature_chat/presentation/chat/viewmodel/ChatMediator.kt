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
    val anchorMessageId: String? = null,
    val anchorPosition: Int = 0,
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
        anchorMessageId: String? = null,
    ) {
        _state.reduce {
            copy(
                currentUserId = currentUserId,
                otherUserId = otherUserId,
                anchorMessageId = anchorMessageId
            )
        }
    }

    fun setAnchorPosition(position: Int) {
        _state.reduce { copy(anchorPosition = position) }
    }

    fun setSelectedMessage(message: Message, isReplying: Boolean = false) {
        _state.reduce {
            copy(selectedMessage = message, isReplying = isReplying)
        }
    }

    fun removeSelectedMessage() {
        _state.reduce {
            copy(selectedMessage = null, isReplying = false)
        }
    }
}
