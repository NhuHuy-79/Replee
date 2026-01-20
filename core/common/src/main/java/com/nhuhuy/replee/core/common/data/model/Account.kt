package com.nhuhuy.replee.core.common.data.model

import com.nhuhuy.replee.core.database.entity.account.AccountEntity
import com.nhuhuy.replee.core.firebase.data.AccountDTO
import com.nhuhuy.replee.core.firebase.utils.toMilliseconds

data class Account(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val createAt: Long? = null,
    val currentToken: String = "",
    val blockedList: List<String> = emptyList()
)

fun AccountEntity.toAccount() = Account(
    id = uid,
    name = name,
    email = email,
    blockedList = blockedUserList
)

fun AccountDTO.toAccount() = Account(
    id = id,
    name = name,
    email = email,
    createAt = createAt?.toMilliseconds(),
    blockedList = blockedList
)

fun AccountDTO.toAccountEntity() = AccountEntity(
    uid = id,
    name = name,
    email = email,
    createAt = createAt?.toMilliseconds(),
    blockedUserList = blockedList,
    logOut = false
)

