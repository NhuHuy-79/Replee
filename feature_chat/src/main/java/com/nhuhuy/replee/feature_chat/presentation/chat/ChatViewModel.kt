@file:OptIn(ExperimentalCoroutinesApi::class)

package com.nhuhuy.replee.feature_chat.presentation.chat

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.nhuhuy.core.domain.model.onFailure
import com.nhuhuy.core.domain.model.onSuccess
import com.nhuhuy.core.domain.usecase.GetAccountByIdUseCase
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.core.common.data.UriConverter
import com.nhuhuy.replee.feature_chat.domain.usecase.block.CheckBlockUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.block.UnblockUserUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.conversation.GetConversationUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.listener.ListenMessageChangeUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.listener.UpdateMessageChangeUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.ObserveBlockStatusUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.PagingMessagesUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.ReadMessageUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.SendImageUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.SendMessageUseCase
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatAction
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatEvent
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatEvent.NavigateToInformation
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel(assistedFactory = ChatViewModel.Factory::class)
class ChatViewModel @AssistedInject constructor(
    @Assisted("otherUserId") private val otherUserId: String,
    @Assisted("currentUserId") private val currentUserId: String,
    private val uriConverter: UriConverter,
    private val readMessageUseCase: ReadMessageUseCase,
    private val unblockUserUseCase: UnblockUserUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val observeBlockStatusUseCase: ObserveBlockStatusUseCase,
    private val listenMessageChangeUseCase: ListenMessageChangeUseCase,
    private val updateMessageChangeUseCase: UpdateMessageChangeUseCase,
    private val getConversationUseCase: GetConversationUseCase,
    private val pagingMessagesUseCase: PagingMessagesUseCase,
    private val checkBlockUseCase: CheckBlockUseCase,
    private val sendImageUseCase: SendImageUseCase,
) : BaseViewModel<ChatAction, ChatEvent, ChatState>() {
    private val conversationId
        get() = createConversationId(
            uid1 = currentUserId,
            uid2 = otherUserId
        )
    val blocked = observeBlockStatusUseCase(ownerId = currentUserId, otherUserId = otherUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)
    private val _state = MutableStateFlow(ChatState(currentUserId = currentUserId))
    private var listenJob: Job? = null

    val pagedMessages = pagingMessagesUseCase(conversationId)
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
            launch {
                isOwnerBLock()
                val otherUser = getAccountByIdUseCase(uid = otherUserId)
                _state.reduce { copy(otherUser = otherUser) }
            }

            launch {
                getConversationUseCase(ownerId = currentUserId, otherUserId = otherUserId)
            }
        }
    }

    private suspend fun isOwnerBLock() {
        val blocked = checkBlockUseCase(ownerId = currentUserId, otherUserId = otherUserId)
        _state.reduce { copy(isBlocked = blocked) }
    }

    private fun listenToMessageChange() {
        listenJob?.cancel()
        listenJob = viewModelScope.launch(Dispatchers.IO) {
            Timber.d("Message Listen Thread: ${Thread.currentThread()}")
            Timber.d("Start Listen to Message")

            listenMessageChangeUseCase(conversationId = conversationId)
                .collect { dataChanges ->
                    Timber.d("Message Change: $dataChanges")
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
                    _state.reduce { copy(messageInput = "") }
                    sendMessageUseCase(
                        senderId = currentUserId,
                        receiverId = otherUserId,
                        conversationId = conversationId,
                        text = messageInput
                    )

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
                            otherUserEmail = otherUser.email,
                            otherUserImg = otherUser.imageUrl
                        )
                    )
                }

                is ChatAction.OnMessageDelete -> TODO()
                is ChatAction.OnMessagePin -> TODO()
                ChatAction.OnUnblockUser -> {
                    unblockUserUseCase(otherUserId = otherUserId)
                }

                is ChatAction.OnImageSend -> {
                    val byteArray = uriConverter.toByteArray(action.uri)

                    if (byteArray != null) {
                        //Send Uri
                        sendImageUseCase(
                            senderId = currentUserId,
                            receiverId = otherUserId,
                            byteArray = byteArray,
                            conversationId = conversationId
                        )
                            .onFailure {
                                Timber.e("Send image failed")
                                onEvent(ChatEvent.SendImage.Failure)
                            }
                            .onSuccess {
                                Timber.d("Send image successfully!")
                                onEvent(ChatEvent.SendImage.Success)
                            }
                    } else {
                        onEvent(ChatEvent.SendImage.Failure)
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

    override fun onCleared() {
        Timber.e("ViewModel Cancel and Stop Listening")
        listenJob?.cancel()
        listenJob = null
        super.onCleared()
    }

    private fun createConversationId(uid1: String, uid2: String): String {
        return listOf(uid1, uid2).sorted().joinToString(separator = "_")
    }

}