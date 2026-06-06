package com.nhuhuy.replee.core.model.validate

sealed interface AuthenticatedState {
    data object Loading : AuthenticatedState
    data class Authenticated(val uid: String) : AuthenticatedState
    data object Unauthenticated : AuthenticatedState
}
