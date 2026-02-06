package com.nhuhuy.core.domain.model

data class Account(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val createAt: Long? = null,
    val currentToken: String = "",
    val blockedList: List<String> = emptyList()
)