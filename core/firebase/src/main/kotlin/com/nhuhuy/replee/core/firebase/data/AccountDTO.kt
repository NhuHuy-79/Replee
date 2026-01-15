package com.nhuhuy.replee.core.firebase.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class AccountDTO(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    @ServerTimestamp
    val createAt: Timestamp? = null,
    val currentToken: String = "",
)


