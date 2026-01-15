package com.nhuhuy.replee.feature_chat.presentation.chat

import androidx.lifecycle.viewModelScope
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.error_handling.onSuccess
import com.nhuhuy.replee.core.common.error_handling.onSuccessSuspend
import com.nhuhuy.replee.core.common.repository.AccountRepository
import com.nhuhuy.replee.core.design_system.state.ScreenState
import com.nhuhuy.replee.core.design_system.state.toScreenState
import com.nhuhuy.replee.core.firebase.network.model.ConversationMessage
import com.nhuhuy.replee.feature_chat.data.SendMessageServiceImp
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatAction
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatEvent
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

@HiltViewModel(assistedFactory = ChatViewModel.Factory::class)
class ChatViewModel @AssistedInject constructor(
    @Assisted("otherUserId") private val otherUserId: String,
    @Assisted("currentUserId") private val currentUserId: String,
    @Assisted("conversationId") private val conversationId: String,
    private val sendMessageServiceImp: SendMessageServiceImp,
    private val accountRepository: AccountRepository,
    private val messageRepository: MessageRepository,
) : BaseViewModel<ChatAction, ChatEvent, ChatState>() {

    private val _state = MutableStateFlow(ChatState(currentUserId = currentUserId))

    override val state: StateFlow<ChatState>
        get() = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val otherUser = accountRepository.getAccountById(uid = otherUserId)
            _state.reduce {
                copy(otherUser = otherUser)
            }
        }
        observeMessageFromNetwork()
    }

    private fun observeMessageFromNetwork(){
       viewModelScope.launch {
           messageRepository.listenFromNetwork(conversationId)
               .collect { resource ->
                   if (resource is Resource.Success){
                       messageRepository.saveMessageToLocal(resource.data)
                   }
               }
       }
    }

    val messageList: StateFlow<List<Message>> = messageRepository.observeConversationMessages(conversationId)
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
                    val message = Message(
                        conversationId = conversationId,
                        messageId = UUID.randomUUID().toString(),
                        senderId = currentUserId,
                        receiverId = otherUserId,
                        content = _state.value.messageInput,
                        seen = false,
                        sentAt = System.currentTimeMillis()
                    )

                    val screenState = messageRepository.addNewMessage(
                        conversationId = conversationId,
                        message = message
                    )
                        .onSuccessSuspend { message ->
                            _state.reduce { copy(messageInput = "") }
                            sendMessageServiceImp.sendMessage(message)
                        }
                        .toScreenState()

                    _state.reduce {
                        copy(sendMessageState = screenState)
                    }
                    delay(1500)
                    _state.reduce {
                        copy(sendMessageState = ScreenState.Idle)
                    }

                }

                ChatAction.OnBackClick -> onEvent(ChatEvent.NavigateBack)
                is ChatAction.OnReadMessage -> {
                    messageRepository.markMessageAsRead(
                        messageIds = action.ids.toList(),
                        conversationId = conversationId,
                        receiverId = currentUserId
                    )
                }

                ChatAction.OnMoreClick -> {
                    val otherUser = state.value.otherUser
                    onEvent(
                        ChatEvent.NavigateToInformation(
                            otherUserId = otherUserId,
                            otherUserName = otherUser.name,
                            otherUserEmail = otherUser.email
                        )
                    )
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