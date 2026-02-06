@file:OptIn(ExperimentalCoroutinesApi::class)

package com.nhuhuy.replee.feature_chat.presentation.chat

import androidx.lifecycle.viewModelScope
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.usecase.GetAccountByIdUseCase
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.core.design_system.state.ScreenState
import com.nhuhuy.replee.core.design_system.state.toScreenState
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.domain.usecase.conversation_setting.UnblockUserUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.LoadMessageUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.ObserveBlockStatusUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.ObserveMessageUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.ReadMessageUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.SaveMessageUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.SendMessageUseCase
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatAction
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatEvent
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatEvent.NavigateToInformation
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ChatViewModel.Factory::class)
class ChatViewModel @AssistedInject constructor(
    @Assisted("otherUserId") private val otherUserId: String,
    @Assisted("currentUserId") private val currentUserId: String,
    @Assisted("conversationId") private val conversationId: String,
    private val readMessageUseCase: ReadMessageUseCase,
    private val unblockUserUseCase: UnblockUserUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val loadMessageUseCase: LoadMessageUseCase,
    private val saveMessageUseCase: SaveMessageUseCase,
    private val observeMessageUseCase: ObserveMessageUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val observeBlockStatusUseCase: ObserveBlockStatusUseCase
) : BaseViewModel<ChatAction, ChatEvent, ChatState>() {
    val
            blocked =
        observeBlockStatusUseCase(ownerId = currentUserId, otherUserId = otherUserId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)
    private val _state = MutableStateFlow(ChatState(currentUserId = currentUserId))

    override val state: StateFlow<ChatState>
        get() = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val otherUser = getAccountByIdUseCase(uid = otherUserId)
            _state.reduce {
                copy(otherUser = otherUser)
            }
        }
        observeMessageFromNetwork()
    }

    private fun observeMessageFromNetwork(){
        blocked.flatMapLatest { blocked ->
            if (blocked) {
                emptyFlow()
            } else {
                observeMessageUseCase(conversationId = conversationId)
            }
        }
            .onEach { result ->
                if (result is NetworkResult.Success) {
                    saveMessageUseCase(result.data)
                }
            }
            .launchIn(viewModelScope)
    }

    val messageList: StateFlow<List<Message>> = loadMessageUseCase(conversationId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    override fun onAction(action: ChatAction) {
        viewModelScope.launch {
            when (action) {
                is ChatAction.OnMessageInputChanged -> {
                    _state.reduce { copy(messageInput = action.messageInput) }
                }

                ChatAction.OnSendMessageClicked -> {
                    _state.reduce {
                        copy(sendMessageState = ScreenState.Loading)
                    }
                    val messageInput: String = state.value.messageInput
                    val screenState = sendMessageUseCase(
                        senderId = currentUserId,
                        receiverId = otherUserId,
                        conversationId = conversationId,
                        text = messageInput
                    ).toScreenState()

                    _state.reduce {
                        copy(sendMessageState = screenState, messageInput = "")
                    }
                    delay(1500)
                    _state.reduce {
                        copy(sendMessageState = ScreenState.Idle)
                    }

                }

                ChatAction.OnBackClick -> onEvent(ChatEvent.NavigateBack)
                is ChatAction.OnReadMessage -> {
                    readMessageUseCase(
                        messageIds = action.ids.toList(),
                        conversationId = conversationId,
                        receiverId = otherUserId
                    )
                }

                ChatAction.OnMoreClick -> {
                    val otherUser = state.value.otherUser
                    onEvent(
                        NavigateToInformation(
                            currentUserId = currentUserId,
                            conversationId = conversationId,
                            otherUserId = otherUserId,
                            otherUserName = otherUser.name,
                            otherUserEmail = otherUser.email
                        )
                    )
                }

                is ChatAction.OnMessageDelete -> TODO()
                is ChatAction.OnMessagePin -> TODO()
                ChatAction.OnUnblockUser -> {
                    unblockUserUseCase(otherUserId = otherUserId)
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("otherUserId") otherUserId: String,
            @Assisted("currentUserId") currentUserId: String,
            @Assisted("conversationId") conversationId: String
        ): ChatViewModel
    }

}