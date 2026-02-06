package com.nhuhuy.replee.feature_chat.presentation.conversation

import androidx.lifecycle.viewModelScope
import com.nhuhuy.core.domain.model.onFailure
import com.nhuhuy.core.domain.model.onSuccess
import com.nhuhuy.core.domain.usecase.GetCurrentAccountUseCase
import com.nhuhuy.core.domain.usecase.SearchAccountByEmailUseCase
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.core.common.toRemoteFailure
import com.nhuhuy.replee.core.design_system.state.ScreenState
import com.nhuhuy.replee.core.design_system.state.toScreenState
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import com.nhuhuy.replee.feature_chat.domain.usecase.conversation.GetConversationUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.conversation.LoadConversationUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.conversation.ObserveConversationsUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.conversation.SaveConversationListUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.conversation.SaveConversationUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.conversation.SaveConversationUserUseCase
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.BottomSheet
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationAction
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationEvent
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationEvent.GoToProfile
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationEvent.NavigateToChatRoom
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationState
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.SynchronizingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val getConversationUseCase: GetConversationUseCase,
    private val loadConversationUseCase: LoadConversationUseCase,
    private val saveConversationListUseCase: SaveConversationListUseCase,
    private val saveConversationUserUseCase: SaveConversationUserUseCase,
    private val observeConversationsUseCase: ObserveConversationsUseCase,
    private val saveConversationUseCase: SaveConversationUseCase,
    private val getCurrentAccountUseCase: GetCurrentAccountUseCase,
    private val searchAccountByEmailUseCase: SearchAccountByEmailUseCase
) : BaseViewModel<ConversationAction, ConversationEvent, ConversationState>() {
    private var firstSync = false
    private val _state = MutableStateFlow(ConversationState())
    override val state: StateFlow<ConversationState>
        get() = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeConversationFromNetwork()
            val currentUser = getCurrentAccountUseCase()
            saveConversationUserUseCase(currentUser.id)
            _state.reduce {
                copy(currentUser = currentUser)
            }
        }
    }

    private fun observeConversationFromNetwork() {
        viewModelScope.launch {
            observeConversationsUseCase().collect { result ->
                if (firstSync) {
                    _state.reduce {
                        copy(synchronizingState = SynchronizingState.SYNC)
                    }
                    firstSync = false
                }
                result.onSuccess { conversation ->
                    saveConversationUseCase(conversation)
                    _state.reduce {
                        copy(
                            synchronizingState = SynchronizingState.NONE
                        )
                    }
                }.onFailure {
                    _state.reduce {
                        copy(
                            synchronizingState = SynchronizingState.FAILURE
                        )
                    }
                }
            }
        }
    }

    val conversationState: StateFlow<ScreenState<List<Conversation>>> =
        loadConversationUseCase().map { list ->
            ScreenState.Success(list)
        }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), ScreenState.Loading
        )

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
                    val result = searchAccountByEmailUseCase(email = query)
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
                    //Avoid spamming retry button
                    val synchronizingState = state.value.synchronizingState
                    if (synchronizingState == SynchronizingState.SYNC) return@launch
                    synchronizeInitialData()
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
                    getConversationUseCase(action.account)
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
                        .onFailure { throwable ->
                            onEvent(
                                ConversationEvent.Error(throwable.toRemoteFailure())
                            )
                        }
                }

                ConversationAction.OnOwnerClick -> {
                    onEvent(GoToProfile)
                }
            }
        }
    }

    private suspend fun synchronizeInitialData() {
        _state.reduce {
            copy(
                synchronizingState = SynchronizingState.SYNC
            )
        }
        saveConversationListUseCase()
            .onSuccess {
                _state.reduce {
                    copy(
                        synchronizingState = SynchronizingState.NONE
                    )
                }
            }
            .onFailure {
                _state.reduce {
                    copy(
                        synchronizingState = SynchronizingState.FAILURE
                    )
                }
            }

    }
}