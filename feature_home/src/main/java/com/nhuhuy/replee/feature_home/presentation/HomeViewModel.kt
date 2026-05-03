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
import com.nhuhuy.replee.core.sync.usecase.conversation.SyncConversationUsersUseCase
import com.nhuhuy.replee.core.sync.usecase.conversation.SyncConversationsUseCase
import com.nhuhuy.replee.feature_home.domain.usecase.account.SetUserOnlineUseCase
import com.nhuhuy.replee.feature_home.domain.usecase.account.UpdateCurrentAccountUseCase
import com.nhuhuy.replee.feature_home.domain.usecase.conversation.GetSearchHistoryUseCase
import com.nhuhuy.replee.feature_home.domain.usecase.conversation.ObserveLocalConversationListUseCase
import com.nhuhuy.replee.feature_home.domain.usecase.conversation.SaveConversationListUseCase
import com.nhuhuy.replee.feature_home.presentation.state.HomeAction
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

@HiltViewModel(assistedFactory = HomeViewModel.Factory::class)
class HomeViewModel @AssistedInject constructor(
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
) : BaseViewModel<HomeAction, com.nhuhuy.replee.feature_home.presentation.state.HomeEvent, com.nhuhuy.replee.feature_home.presentation.state.HomeState>() {
    private val _state =
        MutableStateFlow(com.nhuhuy.replee.feature_home.presentation.state.HomeState())
    override val state: StateFlow<com.nhuhuy.replee.feature_home.presentation.state.HomeState>
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

    override fun onAction(action: HomeAction) {
        viewModelScope.launch {
            when (action) {
                HomeAction.OnAddFabClick -> {
                    _state.reduce {
                        copy(bottomSheet = com.nhuhuy.replee.feature_home.presentation.state.BottomSheet.OPEN)
                    }
                }

                is HomeAction.OnHomeClick -> {
                    val conversation = action.conversation
                    onEvent(
                        com.nhuhuy.replee.feature_home.presentation.state.HomeEvent.NavigateToChatRoom(
                            currentUserId = currentUserId,
                            otherUserId = conversation.otherUserId
                        )
                    )
                }

                HomeAction.OnDismissPress -> {
                    _state.reduce {
                        copy(
                            bottomSheet = com.nhuhuy.replee.feature_home.presentation.state.BottomSheet.CLOSE,
                            dialog = com.nhuhuy.replee.feature_home.presentation.state.Dialog.NONE
                        )
                    }
                }

                HomeAction.OnSearch -> {
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

                HomeAction.OnSearchBarClose -> {
                    _state.reduce {
                        copy(
                            expandSearchBar = false,
                            searchQuery = "",
                            searchState = ScreenState.Idle
                        )
                    }
                }

                HomeAction.Retry -> {
                    //Avoid spamming retry button
                    val synchronizingState = state.value.synchronizingState
                    if (synchronizingState == com.nhuhuy.replee.feature_home.presentation.state.SynchronizingState.SYNC) return@launch
                    synchronizeInitialData()
                }

                is HomeAction.OnQueryChange -> {
                    _state.reduce {
                        copy(searchQuery = action.value)
                    }
                }

                is HomeAction.OnExpandChange -> {
                    _state.reduce {
                        copy(expandSearchBar = action.expand)
                    }
                    if (!action.expand) {
                        _state.reduce {
                            copy(searchQuery = "", searchState = ScreenState.Idle)
                        }
                    }
                }

                is HomeAction.OnAvatarClick -> {
                    onEvent(
                        com.nhuhuy.replee.feature_home.presentation.state.HomeEvent.NavigateToChatRoom(
                            currentUserId = currentUserId,
                            otherUserId = action.account.id
                        )
                    )
                }

                HomeAction.OnOwnerClick -> {
                    onEvent(com.nhuhuy.replee.feature_home.presentation.state.HomeEvent.GoToProfile)
                }

                HomeAction.OnImagePress -> {
                    _state.reduce {
                        copy(dialog = com.nhuhuy.replee.feature_home.presentation.state.Dialog.FULL_IMAGE)
                    }
                }

                HomeAction.OnMessageLongPress -> {
                    _state.reduce {
                        copy(dialog = com.nhuhuy.replee.feature_home.presentation.state.Dialog.MESSAGE)
                    }
                }

                is HomeAction.OnSearchResultClick -> {
                    onEvent(
                        com.nhuhuy.replee.feature_home.presentation.state.HomeEvent.NavigateToChatRoom(
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
        ): HomeViewModel
    }

    override fun onCleared() {
        listenConversationJob?.cancel()
        super.onCleared()
    }
}
