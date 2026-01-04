package com.nhuhuy.replee.feature_chat.presentation.conversation

import androidx.lifecycle.viewModelScope
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.core.firebase.repository.AccountRepository
import com.nhuhuy.replee.core.common.error_handling.onSuccess
import com.nhuhuy.replee.core.design_system.state.ScreenState
import com.nhuhuy.replee.core.design_system.state.toScreenState
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationAction
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationEvent
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationEvent.*
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationState
import com.skydoves.flow.operators.restartable.RestartableStateFlow
import com.skydoves.flow.operators.restartable.restartableStateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val accountRepository: AccountRepository
) : BaseViewModel<ConversationAction, ConversationEvent, ConversationState>() {

    init {
        viewModelScope.launch {
            accountRepository.getCurrentAccount()
                .onSuccess { account ->
                    _state.reduce {
                        copy(currentUser = account)
                    }
                }

        }
    }

    private val _state = MutableStateFlow(ConversationState())
    override val state: StateFlow<ConversationState>
        get() = _state.asStateFlow()

    private val _conversationState: RestartableStateFlow<ScreenState<List<Conversation>>> =
        conversationRepository.observeConversationList().map { resource ->
            resource.toScreenState()
        }.restartableStateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), ScreenState.Loading
        )

    val conversationState: StateFlow<ScreenState<List<Conversation>>> = _conversationState

    override fun onAction(action: ConversationAction) {
        when (action) {
            ConversationAction.OnAddFabClick -> {

            }
            is ConversationAction.OnConversationClick -> {
                onEvent(NavigateToChatRoom(action.conversationId))
            }
            ConversationAction.OnDismissPress -> {
;
            }
            ConversationAction.OnSearchBarClick -> {
                _state.reduce {
                    copy(expandSearchBar = true)
                }
            }

            ConversationAction.OnSearchBarClose -> {
                _state.reduce {
                    copy(expandSearchBar = false)
                }
            }

            ConversationAction.Retry -> {
                _conversationState.restart()
            }

            is ConversationAction.OnQueryChange -> {
                _state.reduce {
                    copy(searchQuery = action.value)
                }
            }

            is ConversationAction.OnExpandChange -> {
                _state.reduce {
                    copy(expandSearchBar = action.expand)
                }
            }

            ConversationAction.OnAvatarClick -> {
                onEvent(ConversationEvent.GoToProfile)
            }
        }
    }

}