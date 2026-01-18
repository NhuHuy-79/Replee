package com.nhuhuy.replee.core.firebase.data_source

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import com.nhuhuy.replee.core.firebase.data.AccountDTO
import com.nhuhuy.replee.core.firebase.data.Constant
import com.nhuhuy.replee.core.firebase.utils.FirestoreCannotConvertObjectException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AccountNetworkDataSource @Inject constructor(
    firestore: FirebaseFirestore
){
    private val collection = firestore.collection(Constant.Firestore.USER_COLLECTION)

    suspend fun sendAccount(account: AccountDTO){
        collection.document(account.id).set(account).await()
    }

    suspend fun updateDeviceToken(uid: String, token: String){
        collection.document(uid)
            .update("currentToken", token)
            .await()
    }

    suspend fun fetchAccountById(id: String) : AccountDTO {
        return collection.document(id)
            .get()
            .await()
            .toObject(AccountDTO::class.java) ?: throw FirestoreCannotConvertObjectException()
    }

    suspend fun fetchAccountsByEmail(query: String): List<AccountDTO> {
        return collection
            .orderBy("email")
            .startAt(query.lowercase())
            .endAt(query.lowercase() + "\uf8ff")
            .limit(10)
            .get()
            .await()
            .toObjects<AccountDTO>()
    }
}