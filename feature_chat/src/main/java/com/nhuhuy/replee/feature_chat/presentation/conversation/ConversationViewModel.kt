package com.nhuhuy.replee.feature_chat.presentation.conversation

import androidx.lifecycle.viewModelScope
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.error_handling.onFailure
import com.nhuhuy.replee.core.common.error_handling.onSuccess
import com.nhuhuy.replee.core.common.repository.AccountRepository
import com.nhuhuy.replee.core.design_system.state.ScreenState
import com.nhuhuy.replee.core.design_system.state.toScreenState
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.BottomSheet
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationAction
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationEvent
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationEvent.Error
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationEvent.GoToProfile
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationEvent.NavigateToChatRoom
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val accountRepository: AccountRepository
) : BaseViewModel<ConversationAction, ConversationEvent, ConversationState>() {
    private val _state = MutableStateFlow(ConversationState())
    override val state: StateFlow<ConversationState>
        get() = _state.asStateFlow()

    init {
        observeConversationFromNetwork()
        viewModelScope.launch {
            _state.reduce {
                copy(syncing = true)
            }
            accountRepository.getCurrentAccount()
                .onSuccess { account ->
                    _state.reduce {
                        copy(currentUser = account)
                    }
                }

            _state.reduce {
                copy(syncing = false)
            }
        }
    }

    private fun observeConversationFromNetwork(){
        viewModelScope.launch {
            conversationRepository.listenFromNetwork().collect { resource ->
                if (resource is Resource.Success){
                    conversationRepository.saveConversationToLocal(resource.data)
                }
            }
        }
    }

    val conversationState: StateFlow<List<Conversation>> =
        conversationRepository.observeConversationList()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    override fun onAction(action: ConversationAction) {
        viewModelScope.launch {
            when (action) {
                ConversationAction.OnAddFabClick -> {
                    _state.reduce {
                        copy(bottomSheet = BottomSheet.OPEN)
                    }
                }

                is ConversationAction.OnConversationClick -> {
                    val conversation = action.conversation
                    onEvent(
                        NavigateToChatRoom(
                            conversationId = conversation.id,
                            currentUserId = conversation.owner.uid,
                            otherUserId = conversation.otherUser.uid
                        )
                    )
                }

                ConversationAction.OnDismissPress -> {
                    _state.reduce {
                        copy(bottomSheet = BottomSheet.CLOSE)
                    }
                }

                ConversationAction.OnSearch -> {
                    _state.reduce {
                        copy(searchState = ScreenState.Loading)
                    }
                    val query = state.value.searchQuery
                    val result = accountRepository.getAccountListWithEmail(query)
                    _state.reduce {
                        copy(searchState = result.toScreenState())
                    }
                }

                ConversationAction.OnSearchBarClose -> {
                    _state.reduce {
                        copy(expandSearchBar = false, searchQuery = "")
                    }
                }

                ConversationAction.Retry -> {

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

                is ConversationAction.OnAvatarClick -> {
                    conversationRepository.getOrCreateConversation(action.account)
                        .onSuccess { id ->
                            _state.reduce {
                                copy(expandSearchBar = false, searchQuery = "")
                            }
                            onEvent(
                                NavigateToChatRoom(
                                    conversationId = id,
                                    currentUserId = state.value.currentUser.id,
                                    otherUserId = action.account.id
                                )
                            )
                        }
                        .onFailure { error ->
                            onEvent(Error(error))
                        }
                }

                ConversationAction.OnOwnerClick -> {
                    onEvent(GoToProfile)
                }
            }
        }
    }
}