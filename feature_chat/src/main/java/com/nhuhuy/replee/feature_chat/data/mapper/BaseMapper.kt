package com.nhuhuy.replee.feature_chat.data.mapper

interface BaseMapper<R, D, L>{
    fun fromRemoteToDomain(remote: R) : D
    fun fromRemoteToLocal(local: L) : D
    fun fromLocalToDomain(local: L) : D
    fun fromDomainToRemote(domain: D) : R
}
