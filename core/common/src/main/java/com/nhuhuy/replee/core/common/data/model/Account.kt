package com.nhuhuy.replee.core.common.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Account(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    @ServerTimestamp
    val createAt: Timestamp? = null
)
