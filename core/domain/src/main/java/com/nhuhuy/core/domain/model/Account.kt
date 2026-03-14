package com.nhuhuy.core.domain.model

data class Account(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val imageUrl: String = "",
    val online: Boolean = false,
    val lastActive: Long = -1,
    val createAt: Long? = null,
    val currentToken: String = "",
    val provider: AuthServiceProvider = AuthServiceProvider.EMAIL,
    val blockedList: List<String> = emptyList()
)

enum class AuthServiceProvider {
    GOOGLE,
    EMAIL
}
