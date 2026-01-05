package com.nhuhuy.replee.core.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import com.nhuhuy.replee.core.firebase.utils.toMilliseconds

data class AccountDTO(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    @ServerTimestamp
    val createAt: Timestamp? = null,
    val currentToken: String = ""
)


