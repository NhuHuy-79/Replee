package com.nhuhuy.replee.core.common.mapper

import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.replee.core.database.entity.account.AccountEntity
import com.nhuhuy.replee.core.firebase.data.AccountDTO
import com.nhuhuy.replee.core.firebase.utils.toMilliseconds

fun Account.toAccountEntity() = AccountEntity(
    uid = id,
    name = name,
    email = email,
    createAt = createAt,
)

fun AccountEntity.toAccount() = Account(
    id = uid,
    name = name,
    email = email,
    blockedList = blockedUserList
)

fun Account.toAccountDTO() = AccountDTO(
    id = id,
    name = name,
    email = email,
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