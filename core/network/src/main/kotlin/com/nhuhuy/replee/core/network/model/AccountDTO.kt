package com.nhuhuy.replee.core.network.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import com.nhuhuy.core.domain.model.AuthServiceProvider

data class AccountDTO(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val imageUrl: String = "",
    val online: Boolean = false,
    val lastActive: Long = -1,
    @ServerTimestamp
    val createAt: Timestamp? = null,
    val currentToken: String = "",
    //add field
    val blockedList: List<String> = emptyList(),
    val provider: AuthServiceProvider = AuthServiceProvider.EMAIL,
)


