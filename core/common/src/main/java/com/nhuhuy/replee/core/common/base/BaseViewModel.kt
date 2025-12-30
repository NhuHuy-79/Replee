package com.nhuhuy.replee.core.common.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

interface UiAction

interface UiState

interface UiEvent


abstract class BaseViewModel<A: UiAction, E: UiEvent, S: UiState>() : ViewModel(){
    abstract val state: StateFlow<S>
    private val _event = Channel<E>()
    val event = _event.receiveAsFlow()

    protected fun onEvent(event: E){
        _event.trySend(event)
    }

    abstract fun onAction(action: A)
}

inline fun <T>MutableStateFlow<T>.reduce(
    block: T.() -> T
) {
    return this.update { state ->
        block(state)
    }
}