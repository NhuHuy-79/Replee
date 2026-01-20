package com.nhuhuy.replee.feature_chat.presentation.chat

import androidx.lifecycle.viewModelScope
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.core.common.data.repository.AccountRepository
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.error_handling.onFailureSuspend
import com.nhuhuy.replee.core.common.error_handling.onSuccessSuspend
import com.nhuhuy.replee.core.design_system.state.ScreenState
import com.nhuhuy.replee.core.design_system.state.toScreenState
import com.nhuhuy.replee.feature_chat.data.NotifyServiceImp
import com.nhuhuy.replee.feature_chat.data.SyncManager
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.domain.model.MessageStatus
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatAction
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatEvent
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatEvent.NavigateToInformation
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
    private val sendMessageServiceImp: NotifyServiceImp,
    private val accountRepository: AccountRepository,
    private val messageRepository: MessageRepository,
    private val syncManager: SyncManager,
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
           messageRepository.observeNetworkMessages(conversationId)
               .collect { resource ->
                   if (resource is Resource.Success){
                       messageRepository.saveMessages(resource.data)
                   }
               }
       }
    }

    val messageList: StateFlow<List<Message>> = messageRepository.observeLocalMessages(conversationId)
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
                        sentAt = System.currentTimeMillis(),
                        status = MessageStatus.PENDING
                    )

                    val screenState = messageRepository.sendMessage(
                        conversationId = conversationId,
                        message = message
                    )
                        .onSuccessSuspend { message ->
                            _state.reduce { copy(messageInput = "") }
                            syncManager.updateMessageStatus(
                                messageId = message.messageId,
                                status = MessageStatus.SYNCED
                            )
                            sendMessageServiceImp.sendNotification(message)
                        }
                        .onFailureSuspend {
                            syncManager.updateMessageStatus(
                                messageId = message.messageId,
                                status = MessageStatus.FAILED
                            )
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
                        NavigateToInformation(
                            conversationId = conversationId,
                            otherUserId = otherUserId,
                            otherUserName = otherUser.name,
                            otherUserEmail = otherUser.email
                        )
                    )
                }

                is ChatAction.OnMessageDelete -> TODO()
                is ChatAction.OnMessagePin -> TODO()
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