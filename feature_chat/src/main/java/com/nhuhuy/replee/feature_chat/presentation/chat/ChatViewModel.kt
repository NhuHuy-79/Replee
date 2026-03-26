@file:OptIn(ExperimentalCoroutinesApi::class)

package com.nhuhuy.replee.feature_chat.presentation.chat

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.nhuhuy.core.domain.model.ValidateFileResult
import com.nhuhuy.core.domain.usecase.GetAccountByIdUseCase
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.feature_chat.domain.usecase.block.CheckBlockUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.block.ObserveOwnerIsBlockUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.block.UnblockUserUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.conversation.GetConversationUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.file.SendFileMessageUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.file.ValidateFileSizeUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.ListenMessageChangeUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.ObserveLocalMessagesUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.ReadMessageUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.SendMessageUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.UpdateMessageChangeUseCase
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatAction
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatEvent
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatEvent.NavigateToInformation
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatOverlay.FullImage
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatOverlay.MessageOption
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatOverlay.None
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import timber.log.Timber

@HiltViewModel(assistedFactory = ChatViewModel.Factory::class)
class ChatViewModel @AssistedInject constructor(
    @Assisted("otherUserId") private val otherUserId: String,
    @Assisted("currentUserId") private val currentUserId: String,
    private val readMessageUseCase: ReadMessageUseCase,
    private val unblockUserUseCase: UnblockUserUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val observeOwnerIsBlockUseCase: ObserveOwnerIsBlockUseCase,
    private val listenMessageChangeUseCase: ListenMessageChangeUseCase,
    private val updateMessageChangeUseCase: UpdateMessageChangeUseCase,
    private val getConversationUseCase: GetConversationUseCase,
    private val observeLocalMessagesUseCase: ObserveLocalMessagesUseCase,
    private val checkBlockUseCase: CheckBlockUseCase,
    private val sendFileMessageUseCase: SendFileMessageUseCase,
    private val validateFileSizeUseCase: ValidateFileSizeUseCase,
) : BaseViewModel<ChatAction, ChatEvent, ChatState>() {
    private val conversationId
        get() = createConversationId(
            uid1 = currentUserId,
            uid2 = otherUserId
        )
    val blocked = observeOwnerIsBlockUseCase(ownerId = currentUserId, otherUserId = otherUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)
    private val _state = MutableStateFlow(ChatState(currentUserId = currentUserId))

    val pagedMessages = observeLocalMessagesUseCase(conversationId)
        .cachedIn(viewModelScope)


    override val state: StateFlow<ChatState>
        get() = _state.asStateFlow()

    init {

        loadInitialData()

        //Listen to Message
        listenToMessageChange()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            supervisorScope {
                launch {
                    val blockedDeferred = async {
                        checkBlockUseCase(currentUserId, otherUserId)
                    }

                    val userDeferred = async {
                        getAccountByIdUseCase(otherUserId)
                    }

                    val blocked = blockedDeferred.await()
                    val user = userDeferred.await()

                    _state.reduce {
                        copy(
                            isBlocked = blocked,
                            otherUser = user
                        )
                    }
                }

                launch {
                    getConversationUseCase(currentUserId, otherUserId)
                }

                launch {
                    readMessageUseCase(
                        conversationId = conversationId,
                        receiverId = currentUserId
                    )
                }
            }
        }
    }

    private fun listenToMessageChange() {
        viewModelScope.launch {
            listenMessageChangeUseCase(conversationId = conversationId)
                .collect { dataChanges ->
                    updateMessageChangeUseCase(dataChanges)
                }
        }

    }

    override fun onAction(action: ChatAction) {
        viewModelScope.launch {
            when (action) {
                is ChatAction.OnMessageInputChanged -> {
                    _state.reduce { copy(messageInput = action.messageInput) }
                }

                ChatAction.OnSendMessageClicked -> {
                    val messageInput: String = state.value.messageInput
                    val currentMessage = state.value.currentMessage

                    Timber.d("Reply message: $currentMessage")

                    sendMessageUseCase(
                        repliedMessage = currentMessage,
                        senderId = currentUserId,
                        receiverId = otherUserId,
                        conversationId = conversationId,
                        text = messageInput
                    )
                    _state.reduce { copy(messageInput = "", currentMessage = null) }
                }

                ChatAction.OnBackClick -> onEvent(ChatEvent.NavigateBack)
                ChatAction.OnMoreClick -> {
                    val otherUser = state.value.otherUser
                    onEvent(
                        NavigateToInformation(
                            currentUserId = currentUserId,
                            conversationId = conversationId,
                            otherUserId = otherUserId,
                            otherUserName = otherUser.name,
                            otherUserEmail = otherUser.email,
                            otherUserImg = otherUser.imageUrl
                        )
                    )
                }

                is ChatAction.OnMessageDelete -> {
                    //TODO("Delete Message)
                    _state.reduce { copy(currentMessage = null, overlay = None) }
                }

                is ChatAction.OnMessagePin -> {
                    //TODO("Pin message)
                    _state.reduce { copy(currentMessage = null, overlay = None) }
                }
                ChatAction.OnUnblockUser -> {
                    unblockUserUseCase(otherUserId = otherUserId)
                }

                is ChatAction.OnImageSend -> {
                    val validateFileResult =
                        validateFileSizeUseCase(uriPath = action.uri.toString())
                    when (validateFileResult) {
                        is ValidateFileResult.FileTooLarge -> {
                            Timber.e("File oo large")
                            onEvent(ChatEvent.FileTooLarge)
                        }

                        is ValidateFileResult.Valid -> {
                            Timber.d("Call")
                            sendFileMessageUseCase(
                                senderId = currentUserId,
                                receiverId = otherUserId,
                                uriPath = action.uri.toString(),
                                conversationId
                            )
                        }

                        is ValidateFileResult.UnSupported -> {
                            onEvent(ChatEvent.UnSupportedFile)
                        }

                        else -> {
                            onEvent(ChatEvent.Unknown)
                        }
                    }
                }

                ChatAction.OnDismiss -> {
                    _state.reduce {
                        copy(overlay = None)
                    }
                }

                is ChatAction.OnImagePress -> {
                    _state.reduce {
                        copy(overlay = FullImage(action.urlKey))
                    }
                }

                is ChatAction.OnMessageLongPress -> {
                    _state.reduce {
                        copy(
                            overlay = MessageOption,
                            isReplying = false,
                            currentMessage = action.message
                        )
                    }
                }

                ChatAction.OnMessageReply -> {
                    _state.reduce {
                        copy(isReplying = true, overlay = None)
                    }
                }

                ChatAction.OnMessageCancelReply -> {
                    _state.reduce {
                        copy(isReplying = false, currentMessage = null)
                    }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("otherUserId") otherUserId: String,
            @Assisted("currentUserId") currentUserId: String,
        ): ChatViewModel
    }

    private fun createConversationId(uid1: String, uid2: String): String {
        return listOf(uid1, uid2).sorted().joinToString(separator = "_")
    }

}