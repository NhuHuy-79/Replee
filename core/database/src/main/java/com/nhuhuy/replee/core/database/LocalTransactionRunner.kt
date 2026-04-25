package com.nhuhuy.replee.core.database

import androidx.room.withTransaction
import javax.inject.Inject

interface LocalTransactionRunner {
    suspend fun runInTransaction(block: suspend () -> Unit)
}

class LocalTransactionRunnerImp @Inject constructor(
    private val coreDatabase: CoreDatabase
) : LocalTransactionRunner {
    override suspend fun runInTransaction(block: suspend () -> Unit) {
        coreDatabase.withTransaction(block)
    }

}