package com.nhuhuy.replee.feature_chat.presentation.conversation

import androidx.lifecycle.viewModelScope
import com.nhuhuy.core.domain.model.onFailure
import com.nhuhuy.core.domain.model.onSuccess
import com.nhuhuy.core.domain.usecase.GetCurrentAccountUseCase
import com.nhuhuy.core.domain.usecase.SearchAccountByEmailUseCase
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.core.design_system.state.ScreenState
import com.nhuhuy.replee.core.design_system.state.toScreenState
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import com.nhuhuy.replee.feature_chat.domain.usecase.conversation.LoadConversationUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.conversation.SaveConversationListUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.conversation.SaveConversationUserUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.listener.ListenConversationUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.listener.UpsertConversationUseCase
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.BottomSheet
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationAction
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationEvent
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationEvent.GoToProfile
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationEvent.NavigateToChatRoom
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationState
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.SynchronizingState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ConversationViewModel.Factory::class)
class ConversationViewModel @AssistedInject constructor(
    @Assisted private val currentUserId: String,
    private val listenConversationUseCase: ListenConversationUseCase,
    private val upsertConversationUseCase: UpsertConversationUseCase,
    private val loadConversationUseCase: LoadConversationUseCase,
    private val saveConversationListUseCase: SaveConversationListUseCase,
    private val saveConversationUserUseCase: SaveConversationUserUseCase,
    private val getCurrentAccountUseCase: GetCurrentAccountUseCase,
    private val searchAccountByEmailUseCase: SearchAccountByEmailUseCase,
) : BaseViewModel<ConversationAction, ConversationEvent, ConversationState>() {
    private val _state = MutableStateFlow(ConversationState())
    override val state: StateFlow<ConversationState>
        get() = _state.asStateFlow()

    init {
        listenToNetworkConversation()
        viewModelScope.launch {
            val currentUser = getCurrentAccountUseCase()
            saveConversationUserUseCase(currentUserId)
            _state.reduce { copy(currentUser = currentUser) }
        }
    }
    val conversationState: StateFlow<ScreenState<List<Conversation>>> =
        loadConversationUseCase().map { list ->
            ScreenState.Success(list)
        }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), ScreenState.Loading
        )

    private fun listenToNetworkConversation() {
        viewModelScope.launch(Dispatchers.IO) {
            listenConversationUseCase(
                ownerId = currentUserId,
                limit = 60
            )
                .distinctUntilChanged()
                .collect { dataChanges ->
                    upsertConversationUseCase(dataChanges)
                }
        }
    }

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
                    onEvent(
                        NavigateToChatRoom(
                            currentUserId = currentUserId,
                            otherUserId = action.account.id
                        )
                    )
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

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted currentUserId: String
        ): ConversationViewModel
    }
}