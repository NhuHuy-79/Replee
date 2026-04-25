package com.nhuhuy.replee.feature_chat.presentation.pin

import androidx.lifecycle.viewModelScope
import com.nhuhuy.core.domain.usecase.GetAccountByIdUseCase
import com.nhuhuy.core.domain.usecase.GetCurrentAccountUseCase
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.feature_chat.domain.usecase.message.ObservePinnedMessageUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.UnPinMessageUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@HiltViewModel(assistedFactory = PinViewModel.Factory::class)
class PinViewModel @AssistedInject constructor(
    @Assisted("conversationId") private val conversationId: String,
    @Assisted("otherUserId") private val otherUserId: String,
    private val observePinnedMessageUseCase: ObservePinnedMessageUseCase,
    private val getCurrentAccountUseCase: GetCurrentAccountUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val unPinMessageUseCase: UnPinMessageUseCase
) : BaseViewModel<PinAction, PinEvent, PinState>() {
    val pinnedMessage = observePinnedMessageUseCase(conversationId)
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds), emptyList())
    private val _state = MutableStateFlow(PinState())

    init {
        viewModelScope.launch {
            val currentAccountDeferred = async { getCurrentAccountUseCase() }
            val otherUserDeferred = async { getAccountByIdUseCase(otherUserId) }
            _state.reduce {
                copy(
                    currentUser = currentAccountDeferred.await(),
                    otherUser = otherUserDeferred.await()
                )
            }
        }
    }

    override val state: StateFlow<PinState>
        get() = _state.asStateFlow()

    override fun onAction(action: PinAction) {
        viewModelScope.launch {
            when (action) {
                PinAction.OnBackPress -> {
                    onEvent(PinEvent.NavigateBack)
                }

                is PinAction.OnPinOff -> {
                    unPinMessageUseCase(action.message)
                }

                is PinAction.OnMessageClick -> {
                    onEvent(
                        PinEvent.NavigateToConversation(
                            currentUserId = state.value.currentUser.id,
                            otherUserId = state.value.otherUser.id,
                            messageId = action.message.messageId
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
            @Assisted("conversationId") conversationId: String
        ): PinViewModel
    }
}