package com.nhuhuy.replee.core.database.base

import androidx.room.Delete
import androidx.room.Upsert

interface BaseDao<E>{

    @Upsert
    suspend fun upsert(entity: E)

    @Delete
    suspend fun delete(entity: E)

    @Upsert
    suspend fun upsertAll(entities: List<E>)

    @Delete
    suspend fun deleteAll(entities: List<E>)
}