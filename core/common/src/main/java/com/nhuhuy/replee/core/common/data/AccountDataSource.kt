package com.nhuhuy.replee.core.common.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import com.nhuhuy.replee.core.common.data.model.Account
import com.nhuhuy.replee.core.common.error_handling.FirestoreCannotConvertObjectException
import com.nhuhuy.replee.core.common.error_handling.FirestoreDataNotFoundException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AccountDataSource @Inject constructor(
    firestore: FirebaseFirestore
){
    private val collection = firestore.collection(Constant.Firestore.USER_COLLECTION)

    suspend fun addAccount(account: Account){
        collection.document(account.id).set(account).await()
    }

    suspend fun getAccountById(id: String) : Account{
        return collection.document(id)
            .get()
            .await()
            .toObject(Account::class.java) ?: throw FirestoreCannotConvertObjectException()
    }

    suspend fun getAccountByEmail(email: String) : Account {
        val accounts = collection
            .get()
            .await()
            .toObjects<Account>()
        return accounts.find { account -> account.email == email } ?: throw FirestoreDataNotFoundException()
    }
}