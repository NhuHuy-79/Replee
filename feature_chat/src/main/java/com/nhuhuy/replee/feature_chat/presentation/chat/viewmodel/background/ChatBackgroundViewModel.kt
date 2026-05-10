package com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.background

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.core.common.di.ChatScopeId
import com.nhuhuy.replee.core.common.di.ScopeHolder
import com.nhuhuy.replee.core.common.utils.ChatIdGenerator
import com.nhuhuy.replee.core.domain.usecase.GetAccountByIdUseCase
import com.nhuhuy.replee.core.sync.domain.usecase.message.SyncMessageUseCase
import com.nhuhuy.replee.feature_chat.data.NotificationManager
import com.nhuhuy.replee.feature_chat.domain.usecase.block.CheckBlockUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.block.ObserveOwnerIsBlockUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.block.UnblockUserUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.conversation.GetConversationUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.GetMessagePositionUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.MarkMessagesReadUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.metadata.GetReadTimeUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.metadata.GetTypingUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.metadata.UpdateReadTimeUseCase
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.ChatMediator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

@Immutable
data class ChatBackgroundCombineState(
    val ownerIsBlock: Boolean = false,
    val otherReadingTime: Long = 0,
    val typingUserIds: List<String> = emptyList(),
) {
    fun isUserTyping(userId: String): Boolean {
        return typingUserIds.contains(userId)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel(assistedFactory = ChatBackgroundViewModel.Factory::class)
class ChatBackgroundViewModel @AssistedInject constructor(
    @param:ChatScopeId private val scopeId: String,
    @Assisted("otherUserId") private val otherUserId: String,
    @Assisted("currentUserId") private val currentUserId: String,
    @Assisted("messageId") private val anchorMessageId: String? = null,
    syncMessageUseCase: SyncMessageUseCase,
    notificationManager: NotificationManager,
    observeOwnerIsBlockUseCase: ObserveOwnerIsBlockUseCase,
    getReadTimeUseCase: GetReadTimeUseCase,
    getTypingUseCase: GetTypingUseCase,
    private val scopeHolder: ScopeHolder,
    private val updateReadTimeUseCase: UpdateReadTimeUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val markMessagesReadUseCase: MarkMessagesReadUseCase,
    private val checkBlockUseCase: CheckBlockUseCase,
    private val getConversationUseCase: GetConversationUseCase,
    private val getMessagePositionUseCase: GetMessagePositionUseCase,
    private val unblockUserUseCase: UnblockUserUseCase,
) : BaseViewModel<ChatBackgroundAction, ChatBackgroundEvent, ChatBackgroundState>() {
    private val _state = MutableStateFlow(ChatBackgroundState())
    override val state = _state.asStateFlow()

    private var currentUserReadingTime = 0L
    private val mediator by lazy {
        scopeHolder.getOrCreateMediator(scopeId = scopeId) { ChatMediator() }
    }

    private val ownerIsBlocked: Flow<Boolean> =
        observeOwnerIsBlockUseCase(ownerId = currentUserId, otherUserId = otherUserId)

    private val otherLastReadingTime: Flow<Long> = getReadTimeUseCase(
        conversationId = ChatIdGenerator.generate(currentUserId, otherUserId),
        otherUserId = otherUserId
    )

    private val typingUserIds: Flow<List<String>> =
        getTypingUseCase(conversationId = ChatIdGenerator.generate(currentUserId, otherUserId))

    val combineState: StateFlow<ChatBackgroundCombineState> = combine(
        ownerIsBlocked,
        otherLastReadingTime,
        typingUserIds
    ) { ownerIsBlocked, otherReadingTime, typingUserIds ->
        ChatBackgroundCombineState(
            ownerIsBlock = ownerIsBlocked,
            otherReadingTime = otherReadingTime,
            typingUserIds = typingUserIds
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ChatBackgroundCombineState())

    init {
        mediator.initializeState(
            currentUserId = currentUserId,
            otherUserId = otherUserId,
            anchorMessageId = anchorMessageId
        )
        val conversationId = mediator.currentState.conversationId
        notificationManager.cancelNotification(notificationId = conversationId.hashCode())

        syncMessageUseCase(conversationId = conversationId)
            .launchIn(viewModelScope)

        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            supervisorScope {
                launch {
                    val anchorPosition = async {
                        anchorMessageId?.let { messageId ->
                            getMessagePositionUseCase(
                                conversationId = mediator.currentState.conversationId,
                                messageId = messageId
                            )
                        }
                    }

                    val blockedDeferred = async {
                        checkBlockUseCase(currentUserId, otherUserId)
                    }

                    val currentUserDataDeferred = async {
                        getAccountByIdUseCase(currentUserId)
                    }

                    val otherUserDeferred = async {
                        getAccountByIdUseCase(otherUserId)
                    }

                    val position = anchorPosition.await() ?: 0
                    mediator.setAnchorPosition(position)

                    _state.reduce {
                        copy(
                            isBlocked = blockedDeferred.await(),
                            otherAccount = otherUserDeferred.await(),
                            currentAccount = currentUserDataDeferred.await()
                        )
                    }
                }

                launch {
                    getConversationUseCase(currentUserId, otherUserId)
                }

                launch {
                    updateReadingTime()
                    markMessagesReadUseCase(
                        conversationId = mediator.currentState.conversationId,
                        receiverId = currentUserId
                    )
                }
            }
        }
    }

    override fun onAction(action: ChatBackgroundAction) {
        when (action) {
            ChatBackgroundAction.OnBackClick -> onEvent(ChatBackgroundEvent.NavigateBack)
            ChatBackgroundAction.OnMoreClick -> {
                onEvent(
                    ChatBackgroundEvent.NavigateToInformation(
                        currentUserId = currentUserId,
                        conversationId = mediator.currentState.conversationId,
                        otherUserId = otherUserId
                    )
                )
            }

            ChatBackgroundAction.OnSearchClick -> {
                onEvent(
                    ChatBackgroundEvent.NavigateToSearch(
                        conversationId = mediator.currentState.conversationId,
                        otherUserId = otherUserId,
                        currentUserId = currentUserId
                    )
                )
            }

            ChatBackgroundAction.OnPinClick -> {
                onEvent(
                    ChatBackgroundEvent.NavigateToPin(
                        conversationId = mediator.currentState.conversationId,
                        otherUserId = otherUserId,
                        currentUserId = currentUserId
                    )
                )
            }

            ChatBackgroundAction.OnUnblockUser -> {
                viewModelScope.launch {
                    unblockUserUseCase(otherUserId = otherUserId)
                    _state.reduce { copy(isBlocked = false) }
                }
            }
            ChatBackgroundAction.OnNewMessageTrigger -> {
                viewModelScope.launch {
                    updateReadingTime()
                }
            }
        }
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

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("otherUserId") otherUserId: String,
            @Assisted("currentUserId") currentUserId: String,
            @Assisted("messageId") anchorMessageId: String? = null
        ): ChatBackgroundViewModel
    }
}
