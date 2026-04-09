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
import com.nhuhuy.replee.feature_chat.domain.usecase.message.DeleteMessageUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.ObserveLocalMessagesUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.PinMessageUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.SendMessageUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.UnPinMessageUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.UpdateUnreadMessageUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.metadata.GetReadTimeUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.metadata.GetTypingUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.metadata.UpdateReadTimeUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.metadata.UpdateTypingUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.sync.SyncMessageUseCase
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatAction
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatEvent
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatEvent.NavigateToInformation
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatEvent.NavigateToPin
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatEvent.NavigateToSearch
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatOverlay.FullImage
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatOverlay.MessageOption
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatOverlay.None
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

@HiltViewModel(assistedFactory = ChatViewModel.Factory::class)
class ChatViewModel @AssistedInject constructor(
    @Assisted("otherUserId") private val otherUserId: String,
    @Assisted("currentUserId") private val currentUserId: String,
    private val updateReadTimeUseCase: UpdateReadTimeUseCase,
    private val updateUnreadMessageUseCase: UpdateUnreadMessageUseCase,
    private val getReadTimeUseCase: GetReadTimeUseCase,
    private val unblockUserUseCase: UnblockUserUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    observeOwnerIsBlockUseCase: ObserveOwnerIsBlockUseCase,
    private val syncMessageUseCase: SyncMessageUseCase,
    private val getConversationUseCase: GetConversationUseCase,
    observeLocalMessagesUseCase: ObserveLocalMessagesUseCase,
    private val checkBlockUseCase: CheckBlockUseCase,
    private val sendFileMessageUseCase: SendFileMessageUseCase,
    private val validateFileSizeUseCase: ValidateFileSizeUseCase,
    private val deleteMessageUseCase: DeleteMessageUseCase,
    private val updateTypingUseCase: UpdateTypingUseCase,
    private val getTypingUseCase: GetTypingUseCase,
    private val pinMessageUseCase: PinMessageUseCase,
    private val unPinMessageUseCase: UnPinMessageUseCase,
) : BaseViewModel<ChatAction, ChatEvent, ChatState>() {
    private var currentUserReadingTime = 0L
    private var listenMessageChangeJob: Job? = null
    private var updateTypingStatusJob: Job? = null
    private var isTyping: Boolean = false
    private val conversationId
        get() = createConversationId(
            uid1 = currentUserId,
            uid2 = otherUserId
        )

    val blocked = observeOwnerIsBlockUseCase(ownerId = currentUserId, otherUserId = otherUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds), false)
    private val _state = MutableStateFlow(ChatState(currentUserId = currentUserId))

    val pagedMessages = observeLocalMessagesUseCase(conversationId)
        .cachedIn(viewModelScope)

    val otherLastReadingTime =
        getReadTimeUseCase(conversationId = conversationId, otherUserId = otherUserId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds), 0L)

    val typingUserIds: StateFlow<List<String>> = getTypingUseCase(conversationId = conversationId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds), emptyList())

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
                    updateReadingTime()
                    updateUnreadMessageUseCase(
                        conversationId = conversationId,
                        receiverId = currentUserId
                    )
                }
            }
        }
    }

    private fun listenToMessageChange() {
        listenMessageChangeJob?.cancel()
        listenMessageChangeJob = syncMessageUseCase(conversationId).launchIn(viewModelScope)
    }

    override fun onAction(action: ChatAction) {
        viewModelScope.launch {
            when (action) {
                is ChatAction.OnMessageInputChanged -> {
                    _state.reduce { copy(messageInput = action.messageInput) }
                    updateTyping()
                    updateReadingTime()
                }

                ChatAction.OnSendMessageClicked -> {
                    val messageInput: String = state.value.messageInput
                    val currentMessage = state.value.currentMessage
                    _state.reduce {
                        copy(
                            messageInput = "",
                            currentMessage = null,
                            isReplying = false
                        )
                    }
                    sendMessageUseCase(
                        repliedMessage = currentMessage,
                        senderId = currentUserId,
                        receiverId = otherUserId,
                        conversationId = conversationId,
                        text = messageInput
                    )

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
                    val currentMessage = _state.value.currentMessage
                    _state.reduce { copy(overlay = None) }

                    currentMessage?.let { message ->
                        deleteMessageUseCase(message = message)
                    }

                    _state.reduce { copy(currentMessage = null) }
                }

                is ChatAction.OnMessagePin -> {
                    val message = _state.value.currentMessage
                    _state.reduce { copy(currentMessage = null, overlay = None) }

                    message?.let {
                        pinMessageUseCase(message = it)
                    }
                }

                is ChatAction.OnMessageUnPin -> {
                    val message = _state.value.currentMessage
                    _state.reduce { copy(currentMessage = null, overlay = None) }
                    message?.let { unPinMessageUseCase(message = it) }
                }

                ChatAction.OnUnblockUser -> {
                    unblockUserUseCase(otherUserId = otherUserId)
                }

                is ChatAction.OnImageSend -> {
                    val repliedMessage = state.value.currentMessage
                    val validateFileResult =
                        validateFileSizeUseCase(uriPath = action.uri.toString())
                    Timber.d("DEBUG_REPLEE: Kết quả validate là: $validateFileResult")
                    when (validateFileResult) {
                        is ValidateFileResult.FileTooLarge -> {
                            Timber.e("File oo large")
                            onEvent(ChatEvent.FileTooLarge)
                        }

                        is ValidateFileResult.Valid -> {
                            Timber.d("Call")
                            _state.reduce {
                                copy(
                                    currentMessage = null,
                                    isReplying = false
                                )
                            }
                            sendFileMessageUseCase(
                                senderId = currentUserId,
                                receiverId = otherUserId,
                                uriPath = action.uri.toString(),
                                conversationId = conversationId,
                                repliedMessage = repliedMessage
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
                            overlay = MessageOption(action.message),
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

                ChatAction.OnNewMessageTrigger -> {
                    updateReadingTime()
                    updateUnreadMessageUseCase(
                        conversationId = conversationId,
                        receiverId = currentUserId
                    )
                }

                ChatAction.OnSearchClick -> {
                    onEvent(
                        NavigateToSearch(
                            conversationId = conversationId,
                            otherUserId = otherUserId
                        )
                    )
                }

                ChatAction.OnPinClick -> {
                    onEvent(
                        NavigateToPin(
                            conversationId = conversationId, otherUserId = otherUserId
                        )
                    )
                }
            }
        }
    }

    private suspend fun updateReadingTime() {
        val currentTime = System.currentTimeMillis()

        if (currentTime > currentUserReadingTime + 3000) {
            updateReadTimeUseCase(
                conversationId = conversationId,
                userId = currentUserId,
                currentTime = currentTime
            )
            currentUserReadingTime = currentTime
        }

    }

    private suspend fun updateTyping() {
        if (!isTyping) {
            isTyping = true
            updateTypingUseCase(
                conversationId = conversationId,
                userId = currentUserId,
                typing = true
            )
        }

        updateTypingStatusJob?.cancel()

        updateTypingStatusJob = viewModelScope.launch {
            delay(5.seconds)
            isTyping = false
            updateTypingUseCase(
                conversationId = conversationId,
                userId = currentUserId,
                typing = false
            )
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

    override fun onCleared() {
        updateTypingStatusJob = null
        viewModelScope.launch(NonCancellable) {
            updateTypingUseCase(
                conversationId = conversationId,
                userId = currentUserId,
                typing = false
            )
        }
        super.onCleared()
    }

}