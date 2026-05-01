package com.nhuhuy.replee.feature_home.presentation

import androidx.lifecycle.viewModelScope
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.base.ScreenState
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.core.data.mapper.toScreenState
import com.nhuhuy.replee.core.domain.usecase.GetCurrentAccountUseCase
import com.nhuhuy.replee.core.domain.usecase.SearchAccountByEmailUseCase
import com.nhuhuy.replee.core.model.chat.Conversation
import com.nhuhuy.replee.core.model.chat.SearchHistoryResult
import com.nhuhuy.replee.core.model.error_handling.onFailure
import com.nhuhuy.replee.core.model.error_handling.onSuccess
import com.nhuhuy.replee.core.sync.usecase.SyncConversationUsersUseCase
import com.nhuhuy.replee.core.sync.usecase.SyncConversationsUseCase
import com.nhuhuy.replee.feature_home.domain.usecase.account.SetUserOnlineUseCase
import com.nhuhuy.replee.feature_home.domain.usecase.account.UpdateCurrentAccountUseCase
import com.nhuhuy.replee.feature_home.domain.usecase.conversation.GetSearchHistoryUseCase
import com.nhuhuy.replee.feature_home.domain.usecase.conversation.ObserveLocalConversationListUseCase
import com.nhuhuy.replee.feature_home.domain.usecase.conversation.SaveConversationListUseCase
import com.nhuhuy.replee.feature_home.presentation.state.ConversationAction
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ConversationViewModel.Factory::class)
class ConversationViewModel @AssistedInject constructor(
    @Assisted private val currentUserId: String,
    private val setUserOnlineUseCase: SetUserOnlineUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val observeLocalConversationListUseCase: ObserveLocalConversationListUseCase,
    private val saveConversationListUseCase: SaveConversationListUseCase,
    private val updateCurrentAccountUseCase: UpdateCurrentAccountUseCase,
    private val getCurrentAccountUseCase: GetCurrentAccountUseCase,
    private val searchAccountByEmailUseCase: SearchAccountByEmailUseCase,
    private val syncConversationUsersUseCase: SyncConversationUsersUseCase,
    private val syncConversationUseCase: SyncConversationsUseCase
) : BaseViewModel<ConversationAction, com.nhuhuy.replee.feature_home.presentation.state.ConversationEvent, com.nhuhuy.replee.feature_home.presentation.state.ConversationState>() {
    private val _state =
        MutableStateFlow(com.nhuhuy.replee.feature_home.presentation.state.ConversationState())
    override val state: StateFlow<com.nhuhuy.replee.feature_home.presentation.state.ConversationState>
        get() = _state.asStateFlow()

    private var listenConversationJob: Job? = null

    val searchHistory: StateFlow<List<SearchHistoryResult>> = getSearchHistoryUseCase(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        listenToNetworkConversation()
        listenToNewConversationUser()
        viewModelScope.launch {
            updateCurrentAccountUseCase(uid = currentUserId)
            setUserOnlineUseCase(uid = currentUserId)
            val currentUser = getCurrentAccountUseCase()
            _state.reduce { copy(currentUser = currentUser) }
        }

    }

    val conversationState: StateFlow<ScreenState<List<Conversation>>> =
        observeLocalConversationListUseCase(ownerId = currentUserId).map { list ->
            ScreenState.Success(list)
        }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), ScreenState.Loading
        )

    private fun listenToNetworkConversation() {
        listenConversationJob?.cancel()
        listenConversationJob = syncConversationUseCase(
            currentUserId = currentUserId,
            limit = 20
        ).launchIn(viewModelScope)
    }

    private fun listenToNewConversationUser() {
        syncConversationUsersUseCase(currentUserId = currentUserId)
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    override fun onAction(action: ConversationAction) {
        viewModelScope.launch {
            when (action) {
                ConversationAction.OnAddFabClick -> {
                    _state.reduce {
                        copy(bottomSheet = com.nhuhuy.replee.feature_home.presentation.state.BottomSheet.OPEN)
                    }
                }

                is ConversationAction.OnConversationClick -> {
                    val conversation = action.conversation
                    onEvent(
                        com.nhuhuy.replee.feature_home.presentation.state.ConversationEvent.NavigateToChatRoom(
                            currentUserId = currentUserId,
                            otherUserId = conversation.otherUserId
                        )
                    )
                }

                ConversationAction.OnDismissPress -> {
                    _state.reduce {
                        copy(
                            bottomSheet = com.nhuhuy.replee.feature_home.presentation.state.BottomSheet.CLOSE,
                            dialog = com.nhuhuy.replee.feature_home.presentation.state.Dialog.NONE
                        )
                    }
                }

                ConversationAction.OnSearch -> {
                    _state.reduce {
                        copy(searchState = ScreenState.Loading)
                    }
                    val query = state.value.searchQuery
                    val result = searchAccountByEmailUseCase(
                        ownerId = currentUserId,
                        email = query
                    )
                    _state.reduce {
                        copy(searchState = result.toScreenState())
                    }
                }

                ConversationAction.OnSearchBarClose -> {
                    _state.reduce {
                        copy(
                            expandSearchBar = false,
                            searchQuery = "",
                            searchState = ScreenState.Idle
                        )
                    }
                }

                ConversationAction.Retry -> {
                    //Avoid spamming retry button
                    val synchronizingState = state.value.synchronizingState
                    if (synchronizingState == com.nhuhuy.replee.feature_home.presentation.state.SynchronizingState.SYNC) return@launch
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
                    if (!action.expand) {
                        _state.reduce {
                            copy(searchQuery = "", searchState = ScreenState.Idle)
                        }
                    }
                }

                is ConversationAction.OnAvatarClick -> {
                    onEvent(
                        com.nhuhuy.replee.feature_home.presentation.state.ConversationEvent.NavigateToChatRoom(
                            currentUserId = currentUserId,
                            otherUserId = action.account.id
                        )
                    )
                }

                ConversationAction.OnOwnerClick -> {
                    onEvent(com.nhuhuy.replee.feature_home.presentation.state.ConversationEvent.GoToProfile)
                }

                ConversationAction.OnImagePress -> {
                    _state.reduce {
                        copy(dialog = com.nhuhuy.replee.feature_home.presentation.state.Dialog.FULL_IMAGE)
                    }
                }

                ConversationAction.OnMessageLongPress -> {
                    _state.reduce {
                        copy(dialog = com.nhuhuy.replee.feature_home.presentation.state.Dialog.MESSAGE)
                    }
                }

                is ConversationAction.OnSearchResultClick -> {
                    onEvent(
                        com.nhuhuy.replee.feature_home.presentation.state.ConversationEvent.NavigateToChatRoom(
                            currentUserId = currentUserId,
                            otherUserId = action.historyResult.uid
                        )
                    )
                }
            }
        }
    }

    private suspend fun synchronizeInitialData() {
        _state.reduce {
            copy(
                synchronizingState = com.nhuhuy.replee.feature_home.presentation.state.SynchronizingState.SYNC
            )
        }
        saveConversationListUseCase()
            .onSuccess {
                _state.reduce {
                    copy(
                        synchronizingState = com.nhuhuy.replee.feature_home.presentation.state.SynchronizingState.NONE
                    )
                }
            }
            .onFailure {
                _state.reduce {
                    copy(
                        synchronizingState = com.nhuhuy.replee.feature_home.presentation.state.SynchronizingState.FAILURE
                    )
                }
            }

    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted currentUserId: String
        ): ConversationViewModel
    }

    override fun onCleared() {
        listenConversationJob?.cancel()
        super.onCleared()
    }
}
