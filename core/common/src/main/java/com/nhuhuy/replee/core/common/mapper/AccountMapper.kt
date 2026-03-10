package com.nhuhuy.replee.core.common.mapper

import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.core.domain.model.AuthServiceProvider
import com.nhuhuy.replee.core.database.entity.account.AccountEntity
import com.nhuhuy.replee.core.network.model.AccountDTO
import com.nhuhuy.replee.core.network.utils.toMilliseconds

fun Account.toAccountEntity() = AccountEntity(
    uid = id,
    name = name,
    email = email,
    createAt = createAt,
    imageUrl = imageUrl,
    provider = provider.name
)

fun AccountEntity.toAccount() = Account(
    id = uid,
    name = name,
    email = email,
    imageUrl = imageUrl,
    blockedList = blockedUserList,
    provider = AuthServiceProvider.valueOf(provider)

)

fun Account.toAccountDTO() = AccountDTO(
    id = id,
    name = name,
    email = email,
    imageUrl = imageUrl,
    provider = provider
)

fun AccountDTO.toAccount() = Account(
    id = id,
    name = name,
    email = email,
    imageUrl = imageUrl,
    provider = provider,
    createAt = createAt?.toMilliseconds(),
    currentToken = currentToken,
    blockedList = blockedList
)

fun AccountDTO.toAccountEntity() = AccountEntity(
    uid = id,
    name = name,
    email = email,
    createAt = createAt?.toMilliseconds(),
    imageUrl = imageUrl,
    provider = provider.name,
    blockedUserList = blockedList,
    logOut = false
)

