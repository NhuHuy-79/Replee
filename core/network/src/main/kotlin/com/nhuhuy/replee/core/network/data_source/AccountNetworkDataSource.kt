package com.nhuhuy.replee.core.network.data_source

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import com.nhuhuy.replee.core.network.model.AccountDTO
import com.nhuhuy.replee.core.network.model.Constant
import com.nhuhuy.replee.core.network.utils.FirestoreCannotConvertObjectException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface AccountNetworkDataSource {
    suspend fun sendAccount(account: AccountDTO)
    suspend fun updateImageUrl(uid: String, imgUrl: String)
    suspend fun updateBlockedList(list: List<String>, owner: String)
    suspend fun updateDeviceToken(uid: String, token: String)
    suspend fun fetchAccountById(id: String): AccountDTO
    suspend fun fetchAccountByIdList(ids: List<String>): List<AccountDTO>
    suspend fun fetchAccountsByEmail(query: String): List<AccountDTO>
}

class AccountNetworkDataSourceImp @Inject constructor(
    firestore: FirebaseFirestore
) : AccountNetworkDataSource {
    private val collection = firestore.collection(Constant.Firestore.USER_COLLECTION)

    override suspend fun sendAccount(account: AccountDTO) {
        collection.document(account.id).set(account).await()
    }

    override suspend fun updateImageUrl(uid: String, imgUrl: String) {
        collection.document(uid)
            .update("imageUrl", imgUrl)
            .await()
    }

    override suspend fun updateBlockedList(list: List<String>, owner: String) {
        collection.document(owner)
            .update("blockedList", list)
            .await()
    }

    override suspend fun updateDeviceToken(uid: String, token: String) {
        collection.document(uid)
            .update("currentToken", token)
            .await()
    }

    override suspend fun fetchAccountById(id: String): AccountDTO {
        return collection.document(id)
            .get()
            .await()
            .toObject(AccountDTO::class.java) ?: throw FirestoreCannotConvertObjectException()
    }

    override suspend fun fetchAccountByIdList(ids: List<String>): List<AccountDTO> {
        return collection.whereIn("id", ids)
            .get()
            .await()
            .toObjects<AccountDTO>()
    }

    override suspend fun fetchAccountsByEmail(query: String): List<AccountDTO> {
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
