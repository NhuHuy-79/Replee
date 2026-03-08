package com.nhuhuy.replee.feature_chat.presentation.search

import androidx.lifecycle.viewModelScope
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.feature_chat.domain.usecase.message.SearchMessageUseCase
import com.nhuhuy.replee.feature_chat.presentation.search.state.SearchAction
import com.nhuhuy.replee.feature_chat.presentation.search.state.SearchEvent
import com.nhuhuy.replee.feature_chat.presentation.search.state.SearchState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val searchMessageUseCase: SearchMessageUseCase
) : BaseViewModel<SearchAction, SearchEvent, SearchState>() {
    private val _state = MutableStateFlow(SearchState())
    override val state: StateFlow<SearchState>
        get() = _state.asStateFlow()

    override fun onAction(action: SearchAction) {
        viewModelScope.launch {
            when (action) {
                is SearchAction.OnQueryChange -> {
                    _state.reduce { copy(query = action.query) }
                }

                SearchAction.OnSearch -> {

                }
            }
        }
    }
}