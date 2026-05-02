package com.nhuhuy.replee.feature_chat.presentation.search

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.core.domain.usecase.GetAccountByIdUseCase
import com.nhuhuy.replee.core.domain.usecase.GetCurrentAccountUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.ObserveMessagesWithQueryUseCase
import com.nhuhuy.replee.feature_chat.presentation.search.state.SearchAction
import com.nhuhuy.replee.feature_chat.presentation.search.state.SearchEvent
import com.nhuhuy.replee.feature_chat.presentation.search.state.SearchState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

@OptIn(FlowPreview::class)
@HiltViewModel(assistedFactory = SearchViewModel.Factory::class)
class SearchViewModel @AssistedInject constructor(
    @Assisted("conversationId") private val conversationId: String,
    @Assisted("currentUserId") private val currentUserId: String,
    @Assisted("otherUerId") private val otherUserId: String,
    private val observeMessagesWithQueryUseCase: ObserveMessagesWithQueryUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val getCurrentAccountUseCase: GetCurrentAccountUseCase,
) : BaseViewModel<SearchAction, SearchEvent, SearchState>() {
    private val _state = MutableStateFlow(SearchState())
    override val state: StateFlow<SearchState>
        get() = _state.asStateFlow()
    private val stateValue get() = state.value

    private val _searchTrigger = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagingMessages = _searchTrigger
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flowOf(PagingData.empty())
            } else {
                observeMessagesWithQueryUseCase(
                    currentUserId = currentUserId,
                    conversationId = conversationId,
                    query = query
                )
            }
        }
        .cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            supervisorScope {
                val currentUserDeferred = async { getCurrentAccountUseCase() }
                val otherUserDeferred = async { getAccountByIdUseCase(otherUserId) }
                val currentUser = currentUserDeferred.await()
                val otherUser = otherUserDeferred.await()
                _state.reduce {
                    copy(
                        currentUser = currentUser,
                        otherUser = otherUser
                    )
                }
            }
        }
    }

    override fun onAction(action: SearchAction) {
        viewModelScope.launch {
            when (action) {
                is SearchAction.OnQueryChange -> {
                    _state.reduce {
                        copy(searchQuery = action.query)
                    }
                }

                SearchAction.OnSearchClose -> {
                    _state.reduce { copy(searchQuery = "") }
                }

                SearchAction.OnNavigateBack -> {
                    onEvent(SearchEvent.NavigateBack)
                }

                is SearchAction.OnMessagePress -> {
                    val message = action.message
                    val currentUserId =
                        if (message.senderId == otherUserId) message.receiverId else message.senderId
                    onEvent(
                        SearchEvent.NavigateToMessage(
                            currentUserId = currentUserId,
                            anchorMessageId = action.message.messageId,
                            anchorSendAt = action.message.sentAt
                        )
                    )
                }

                is SearchAction.OnSearch -> {
                    val query = stateValue.searchQuery
                    _state.reduce { copy(searchQuery = query) }
                    _searchTrigger.value = query
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("conversationId") conversationId: String,
            @Assisted("otherUerId") otherUserId: String,
            @Assisted("currentUserId") currentUserId: String,
        ): SearchViewModel
    }
}
