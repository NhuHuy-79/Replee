package com.nhuhuy.replee.core.network.manager

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import com.google.firebase.firestore.WriteBatch
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await

interface NetworkTransactionRunner {
    suspend fun runInTransaction(block: (Transaction) -> Unit)
    suspend fun runInBatch(block: (WriteBatch) -> Unit)
}

class NetworkTransactionRunnerImp @Inject constructor(
    private val firestore: FirebaseFirestore
) : NetworkTransactionRunner {

    override suspend fun runInTransaction(block: (Transaction) -> Unit) {
        firestore.runTransaction { transaction ->
            block(transaction)
        }.await()
    }

    override suspend fun runInBatch(block: (WriteBatch) -> Unit) {
        firestore.runBatch { batch ->
            block(batch)
        }.await()
    }
}