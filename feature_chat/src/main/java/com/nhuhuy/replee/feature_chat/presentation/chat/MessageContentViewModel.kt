package com.nhuhuy.replee.feature_chat.presentation.chat

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhuhuy.replee.core.common.base.UiAction
import com.nhuhuy.replee.core.common.base.UiState
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.core.model.chat.LocalPathMessage
import com.nhuhuy.replee.core.model.chat.Message
import com.nhuhuy.replee.core.model.error_handling.onSuccess
import com.nhuhuy.replee.feature_chat.domain.repository.PaginatorRepository
import com.nhuhuy.replee.feature_chat.domain.usecase.paging.GetLatestMessagesUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.paging.GetMessageAfterKeyUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.paging.GetMessageBeforeKeyUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.paging.ObserveMessagesUseCase
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

sealed interface MessageAction : UiAction {
    data class JumpToMessageId(val messageId: String) : MessageAction
    data object ScrollToTop : MessageAction
    data object ScrollToBottom : MessageAction
}

@Immutable
data class MessageUiState(
    val pageSize: Int = 20,
    val anchorMessagePosition: Int = 0,
    val anchorMessageId: String? = null,
    val isLoadingTop: Boolean = false,
    val isLoadingBottom: Boolean = false,
    val thresholdTrigger: Int = 5,
    val endOfBottom: Boolean = false,
    val endOfTop: Boolean = false
) : UiState

enum class ScrollPosition {
    MIDDLE, TOP, BOTTOM
}

@HiltViewModel(assistedFactory = MessageContentViewModel.Factory::class)
class MessageContentViewModel @AssistedInject constructor(
    @Assisted("conversationId") private val conversationId: String,
    @Assisted("anchorMessageId") private val anchorMessageId: String? = null,
    private val paginatorRepository: PaginatorRepository,
    private val observeMessagesUseCase: ObserveMessagesUseCase,
    private val getLatestMessagesUseCase: GetLatestMessagesUseCase,
    private val getMessageAfterKeyUseCase: GetMessageAfterKeyUseCase,
    private val getMessageBeforeKeyUseCase: GetMessageBeforeKeyUseCase,
) : ViewModel() {
    private val _beforeTime = MutableStateFlow<Long?>(null)
    private val _afterTime = MutableStateFlow<Long?>(null)

    private val _uiState = MutableStateFlow(MessageUiState(anchorMessageId = anchorMessageId))
    val uiState = _uiState.asStateFlow()
    private val stateValue get() = uiState.value

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
            Timber.e("AUTO UPDATE KEYS: bottom=$bottomKey, top=$topKey | Total: ${localMessages.size}")
        }
    }
        .distinctUntilChanged()
        .map { it.toUiModelsWithSeparators() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

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

    fun onAction(action: MessageAction) {
        when (action) {
            is MessageAction.JumpToMessageId -> detectMessageToJump(messageId = action.messageId)
            MessageAction.ScrollToBottom -> fetchInBottom()
            MessageAction.ScrollToTop -> fetchInTop()
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

                    // Cập nhật beforeTime để Flow observe rộng ra thêm về quá khứ
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

                    // Cập nhật afterTime để Flow observe rộng ra thêm về hiện tại
                    if (_afterTime.value != null) {
                        _afterTime.update { sortedMessages.first().sentAt }
                    }

                    Timber.e("FETCH BOTTOM SUCCESS: New bottomKey=$bottomKey (Newest), count=${sortedMessages.size}")
                }

                val endOfData =
                    networkMessages.isEmpty() || networkMessages.size < stateValue.pageSize
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