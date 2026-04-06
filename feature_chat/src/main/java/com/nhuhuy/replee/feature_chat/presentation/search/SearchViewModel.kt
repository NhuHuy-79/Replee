package com.nhuhuy.replee.feature_chat.presentation.search

import androidx.lifecycle.viewModelScope
import com.nhuhuy.core.domain.usecase.GetAccountByIdUseCase
import com.nhuhuy.core.domain.usecase.GetCurrentAccountUseCase
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.feature_chat.domain.usecase.message.SearchMessageUseCase
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

@HiltViewModel(assistedFactory = SearchViewModel.Factory::class)
class SearchViewModel @AssistedInject constructor(
    @Assisted("conversationId") private val conversationId: String,
    @Assisted("otherUerId") private val otherUserId: String,
    private val searchMessageUseCase: SearchMessageUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val getCurrentAccountUseCase: GetCurrentAccountUseCase,
) : BaseViewModel<SearchAction, SearchEvent, SearchState>() {
    private val _state = MutableStateFlow(SearchState())
    override val state: StateFlow<SearchState>
        get() = _state.asStateFlow()

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

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResults = query
        .flatMapLatest { value ->
            searchMessageUseCase(conversationId = conversationId, query = value)
        }.distinctUntilChanged()
        .debounce(300)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    override fun onAction(action: SearchAction) {
        viewModelScope.launch {
            when (action) {
                is SearchAction.OnQueryChange -> {
                    _query.update { action.query }
                }

                SearchAction.OnSearchClose -> {
                    _query.update { "" }
                }

                SearchAction.OnNavigateBack -> {
                    onEvent(SearchEvent.NavigateBack)
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("conversationId") conversationId: String,
            @Assisted("otherUerId") otherUserId: String,
        ): SearchViewModel
    }
}
