package com.nhuhuy.replee.core.common.mapper

import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.core.domain.model.AuthServiceProvider
import com.nhuhuy.replee.core.database.entity.account.AccountEntity
import com.nhuhuy.replee.core.firebase.data.AccountDTO
import com.nhuhuy.replee.core.firebase.utils.toMilliseconds

fun Account.toAccountEntity() = AccountEntity(
    uid = id,
    name = name,
    email = email,
    createAt = createAt,
    provider = provider.name
)

fun AccountEntity.toAccount() = Account(
    id = uid,
    name = name,
    email = email,
    blockedList = blockedUserList,
    provider = AuthServiceProvider.valueOf(provider)
)

fun Account.toAccountDTO() = AccountDTO(
    id = id,
    name = name,
    email = email,
    provider = provider
)

fun AccountDTO.toAccount() = Account(
    id = id,
    name = name,
    email = email,
    provider = provider,
    createAt = createAt?.toMilliseconds(),
    blockedList = blockedList
)

fun AccountDTO.toAccountEntity() = AccountEntity(
    uid = id,
    name = name,
    email = email,
    createAt = createAt?.toMilliseconds(),
    provider = provider.name,
    blockedUserList = blockedList,
    logOut = false
)