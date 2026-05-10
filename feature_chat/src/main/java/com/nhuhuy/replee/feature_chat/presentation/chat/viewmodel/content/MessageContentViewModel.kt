package com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.content

import androidx.lifecycle.viewModelScope
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.core.common.di.ChatScopeId
import com.nhuhuy.replee.core.common.di.ScopeHolder
import com.nhuhuy.replee.core.model.chat.LocalPathMessage
import com.nhuhuy.replee.core.model.chat.Message
import com.nhuhuy.replee.core.model.error_handling.onSuccess
import com.nhuhuy.replee.feature_chat.domain.repository.PaginatorRepository
import com.nhuhuy.replee.feature_chat.domain.usecase.message.AddReactionUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.DeleteMessageUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.PinMessageUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.RemoveReactionUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.UnPinMessageUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.paging.GetLatestMessagesUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.paging.GetMessageAfterKeyUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.paging.GetMessageBeforeKeyUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.paging.ObserveMessagesUseCase
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.ChatMediator
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.main.ChatOverlay
import com.nhuhuy.replee.feature_chat.utils.toUiModelsWithSeparators
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel(assistedFactory = MessageContentViewModel.Factory::class)
class MessageContentViewModel @AssistedInject constructor(
    @param:ChatScopeId private val scopeId: String,
    @Assisted("conversationId") private val conversationId: String,
    @Assisted("anchorMessageId") private val anchorMessageId: String? = null,
    private val scopeHolder: ScopeHolder,
    private val paginatorRepository: PaginatorRepository,
    private val observeMessagesUseCase: ObserveMessagesUseCase,
    private val getLatestMessagesUseCase: GetLatestMessagesUseCase,
    private val getMessageAfterKeyUseCase: GetMessageAfterKeyUseCase,
    private val getMessageBeforeKeyUseCase: GetMessageBeforeKeyUseCase,
    private val deleteMessageUseCase: DeleteMessageUseCase,
    private val pinMessageUseCase: PinMessageUseCase,
    private val unPinMessageUseCase: UnPinMessageUseCase,
    private val addReactionUseCase: AddReactionUseCase,
    private val removeReactionUseCase: RemoveReactionUseCase,
) : BaseViewModel<MessageContentAction, MessageContentEvent, MessageContentState>() {
    private val mediator by lazy {
        scopeHolder.getOrCreateMediator(scopeId = scopeId) { ChatMediator() }
    }

    private val _beforeTime = MutableStateFlow<Long?>(null)
    private val _afterTime = MutableStateFlow<Long?>(null)

    private val _uiState = MutableStateFlow(MessageContentState(anchorMessageId = anchorMessageId))
    override val state = _uiState.asStateFlow()
    private val stateValue get() = state.value
    private var topKey: String? = null
    private var bottomKey: String? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    val messagesUiFlow = combine(_beforeTime, _afterTime) { before, after ->
        before to after
    }.flatMapLatest { (before, after) ->
        if (before == null || after == null) {
            observeMessagesUseCase(conversationId = conversationId)
        } else {
            observeMessagesUseCase(
                conversationId = conversationId,
                startTime = before,
                endTime = after
            )
        }
    }.onEach { localMessages: List<LocalPathMessage> ->
        if (localMessages.isNotEmpty()) {
            bottomKey = localMessages.first().message.messageId
            topKey = localMessages.last().message.messageId
        }
    }
        .distinctUntilChanged()
        .map { it.toUiModelsWithSeparators() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            if (anchorMessageId != null) {
                detectMessageToJump(anchorMessageId)
            } else {
                getLatestMessagesUseCase(
                    conversationId = conversationId,
                    pageSize = stateValue.pageSize
                ).onSuccess { networkMessages ->
                    Timber.e("FETCH INITIAL FROM SERVER SUCCESS: ${networkMessages.size} items")
                }
            }
        }
    }

    override fun onAction(action: MessageContentAction) {
        when (action) {
            is MessageContentAction.JumpToMessageContentId -> detectMessageToJump(messageId = action.messageId)
            MessageContentAction.ScrollToBottom -> fetchInBottom()
            MessageContentAction.ScrollToTop -> fetchInTop()
            MessageContentAction.OnDismiss -> _uiState.reduce { copy(overlay = ChatOverlay.None) }
            is MessageContentAction.OnImagePress -> _uiState.reduce {
                copy(
                    overlay = ChatOverlay.FullImage(
                        action.urlKey
                    )
                )
            }

            is MessageContentAction.OnMessageContentLongPress -> {
                _uiState.reduce { copy(overlay = ChatOverlay.MessageOption(action.message)) }
                mediator.setSelectedMessage(action.message, isReplying = false)
            }

            MessageContentAction.OnMessageContentReply -> {
                _uiState.reduce { copy(overlay = ChatOverlay.None) }
                mediator.setSelectedMessage(
                    mediator.currentState.selectedMessage ?: return,
                    isReplying = true
                )
            }

            MessageContentAction.OnMessageContentDelete -> {
                val currentMessage = mediator.currentState.selectedMessage ?: return
                _uiState.reduce { copy(overlay = ChatOverlay.None) }
                viewModelScope.launch {
                    deleteMessageUseCase(message = currentMessage)
                    mediator.removeSelectedMessage()
                }
            }

            MessageContentAction.OnMessageContentPin -> {
                val message = mediator.currentState.selectedMessage ?: return
                _uiState.reduce { copy(overlay = ChatOverlay.None) }
                viewModelScope.launch {
                    pinMessageUseCase(message = message)
                    mediator.removeSelectedMessage()
                }
            }

            MessageContentAction.OnMessageContentUnPin -> {
                val message = mediator.currentState.selectedMessage ?: return
                _uiState.reduce { copy(overlay = ChatOverlay.None) }
                viewModelScope.launch {
                    unPinMessageUseCase(message = message)
                    mediator.removeSelectedMessage()
                }
            }

            is MessageContentAction.OnReactionSelect -> {
                val messageId = mediator.currentState.selectedMessage?.messageId ?: return
                _uiState.reduce { copy(overlay = ChatOverlay.None) }
                viewModelScope.launch {
                    addReactionUseCase(
                        conversationId = conversationId,
                        messageId = messageId,
                        reaction = action.reaction,
                        userId = mediator.currentState.currentUserId
                    )
                    mediator.removeSelectedMessage()
                }
            }

            is MessageContentAction.OnReactionDelete -> {
                viewModelScope.launch {
                    removeReactionUseCase(
                        conversationId = conversationId,
                        messageId = action.messageId,
                        reaction = action.reaction,
                        userId = mediator.currentState.currentUserId
                    )
                }
            }

            MessageContentAction.OnReactionMoreClick -> _uiState.reduce { copy(overlay = ChatOverlay.EmojiPicker) }
            is MessageContentAction.OnMessageContentReactionClick -> {
                viewModelScope.launch {
                    removeReactionUseCase(
                        conversationId = conversationId,
                        messageId = action.messageId,
                        reaction = action.reaction,
                        userId = mediator.currentState.currentUserId
                    )
                }
            }
        }
    }

    private fun fetchInTop() {
        if (stateValue.isLoadingTop || stateValue.endOfTop || topKey == null) return

        viewModelScope.launch {
            _uiState.reduce { copy(isLoadingTop = true) }

            getMessageBeforeKeyUseCase(
                conversationId = conversationId,
                anchorMessageId = topKey!!,
                pageSize = stateValue.pageSize,
            ).onSuccess { networkMessages ->
                if (networkMessages.isNotEmpty()) {
                    val sortedMessages = networkMessages.sortedByDescending { it.sentAt }
                    topKey = sortedMessages.last().messageId

                    if (_beforeTime.value != null) {
                        _beforeTime.update { sortedMessages.last().sentAt }
                    }
                    
                    Timber.e("FETCH TOP SUCCESS: New topKey=$topKey (Oldest), count=${sortedMessages.size}")
                }

                val endOfData =
                    networkMessages.isEmpty() || networkMessages.size < stateValue.pageSize
                _uiState.reduce {
                    copy(
                        endOfTop = endOfData,
                        isLoadingTop = false
                    )
                }
            }
        }
    }

    private fun fetchInBottom() {
        if (stateValue.isLoadingBottom || stateValue.endOfBottom || bottomKey == null) return

        viewModelScope.launch {
            _uiState.reduce { copy(isLoadingBottom = true) }

            getMessageAfterKeyUseCase(
                conversationId = conversationId,
                anchorMessageId = bottomKey!!,
                pageSize = stateValue.pageSize,
            ).onSuccess { networkMessages ->
                if (networkMessages.isNotEmpty()) {
                    val sortedMessages = networkMessages.sortedByDescending { it.sentAt }
                    bottomKey = sortedMessages.first().messageId

                    if (_afterTime.value != null) {
                        _afterTime.update { sortedMessages.first().sentAt }
                    }

                    Timber.e("FETCH BOTTOM SUCCESS: New bottomKey=$bottomKey (Newest), count=${sortedMessages.size}")
                }

                val endOfData =
                    networkMessages.isEmpty() || networkMessages.size < stateValue.pageSize

                if (endOfData) {
                    _afterTime.update { null }
                }

                _uiState.reduce {
                    copy(
                        endOfBottom = endOfData,
                        isLoadingBottom = false
                    )
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("conversationId") conversationId: String,
            @Assisted("anchorMessageId") anchorMessageId: String? = null
        ): MessageContentViewModel
    }

    private fun detectMessageToJump(messageId: String) {
        viewModelScope.launch {
            _uiState.reduce { copy(isLoadingTop = true, isLoadingBottom = true) }

            paginatorRepository.fetchMessageBetweenKey(
                conversationId = conversationId,
                key = messageId,
                limit = 15L
            ).onSuccess { messages: List<Message> ->
                if (messages.isNotEmpty()) {
                    val sorted = messages.sortedByDescending { it.sentAt }
                    _beforeTime.update { sorted.last().sentAt }
                    _afterTime.update { sorted.first().sentAt }
                    _uiState.reduce { copy(anchorMessageId = messageId) }
                }
                _uiState.reduce { copy(isLoadingTop = false, isLoadingBottom = false) }
            }
        }
    }

    private fun restorePaging() {
        _beforeTime.update { null }
        _afterTime.update { null }
        _uiState.reduce { copy(anchorMessageId = null) }
    }
}
