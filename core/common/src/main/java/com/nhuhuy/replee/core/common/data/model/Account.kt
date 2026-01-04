package com.nhuhuy.replee.core.common.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import com.nhuhuy.replee.core.firebase.AccountDTO
import com.nhuhuy.replee.core.firebase.toMilliseconds

data class Account(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val createAt: Long? = null,
    val currentToken: String = ""
)

fun AccountDTO.toAccount() : Account{
    return Account(
        id = id,
        name = name,
        email = email,
        createAt = createAt?.toMilliseconds()
    )
}
