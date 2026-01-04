package com.nhuhuy.replee.core.common.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import com.nhuhuy.replee.core.common.error_handling.FirestoreCannotConvertObjectException
import com.nhuhuy.replee.core.common.error_handling.FirestoreDataNotFoundException
import com.nhuhuy.replee.core.firebase.AccountDTO
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AccountNetworkDataSource @Inject constructor(
    firestore: FirebaseFirestore
){
    private val collection = firestore.collection(Constant.Firestore.USER_COLLECTION)

    suspend fun addAccount(account: AccountDTO){
        collection.document(account.id).set(account).await()
    }

    suspend fun getAccountById(id: String) : AccountDTO{
        return collection.document(id)
            .get()
            .await()
            .toObject(AccountDTO::class.java) ?: throw FirestoreCannotConvertObjectException()
    }

    suspend fun getAccountByEmail(email: String) : AccountDTO {
        val accounts = collection
            .get()
            .await()
            .toObjects<AccountDTO>()
        return accounts.find { account -> account.email == email } ?: throw FirestoreDataNotFoundException()
    }
}