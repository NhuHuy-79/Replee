package com.nhuhuy.replee.core.firebase.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import com.nhuhuy.core.domain.model.AuthServiceProvider

data class AccountDTO(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val imgUrl: String = "",
    @ServerTimestamp
    val createAt: Timestamp? = null,
    val currentToken: String = "",
    //add field
    val blockedList: List<String> = emptyList(),
    val provider: AuthServiceProvider = AuthServiceProvider.EMAIL,
)


