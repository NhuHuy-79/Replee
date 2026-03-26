package com.nhuhuy.replee.feature_chat.data.source.conversation

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.nhuhuy.replee.core.network.model.Constant
import com.nhuhuy.replee.core.network.model.DataChange
import com.nhuhuy.replee.core.network.model.observeDataChange
import com.nhuhuy.replee.core.network.utils.optimizedWrite
import com.nhuhuy.replee.feature_chat.data.model.network.ConversationDTO
import com.nhuhuy.replee.feature_chat.data.model.network.MessageDTO
import com.nhuhuy.replee.feature_chat.utils.updateFieldValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

interface ConversationNetworkDataSource {
    suspend fun deleteAllUnreadMessages(conversationId: String, uid: String)
    suspend fun updateUnreadMessageCount(conversationId: String, receiverId: String, count: Int)
    suspend fun sendConversation(conversationDTO: ConversationDTO)
    suspend fun getConversationUserIdsWithOwner(ownerId: String): List<String>
    suspend fun fetchConversationsByUser(uid: String): List<ConversationDTO>
    suspend fun fetchConversationById(conversationId: String): ConversationDTO?
    suspend fun fetchConversationByIdOrThrow(conversationId: String): ConversationDTO
    suspend fun sendConversations(conversationDTOList: List<ConversationDTO>)
    suspend fun updateNicknameForUser(
        uid: String,
        nickName: String,
        conversationDTO: ConversationDTO
    )

    suspend fun updateConversationDataMap(dataMaps: List<Map<String, Any>>)

    suspend fun updateLastMessage(message: MessageDTO, conversation: ConversationDTO)
    suspend fun updateSeedColor(conversationId: String, seedColor: Long)
    suspend fun updateMutedStatus(conversationId: String, uid: String, muted: Boolean)
    suspend fun updatePinnedStatus(conversationId: String, uid: String, pinned: Boolean)
    fun streamConversationsByOwner(ownerId: String): Flow<List<ConversationDTO>>
    fun listenConversationChangesByOwner(ownerId: String): Flow<List<DataChange<ConversationDTO>>>
    fun listenConversationChanges(
        ownerId: String,
        limit: Int
    ): Flow<List<DataChange<ConversationDTO>>>
}

class ConversationNetworkDataSourceImp @Inject constructor(
    private val firestore: FirebaseFirestore,
) : ConversationNetworkDataSource {
    private val collection = firestore.collection(Constant.Firestore.CONVERSATION_COLLECTION)
    override suspend fun deleteAllUnreadMessages(
        conversationId: String,
        uid: String
    ) {
        val field = mapOf("unReadMessages.${uid}" to 0)
        collection.document(conversationId)
            .update(field)
            .await()

    }

    override suspend fun updateUnreadMessageCount(
        conversationId: String,
        receiverId: String,
        count: Int
    ) {
        collection.document(conversationId)
            .update("unReadMessages.$receiverId", 0)
            .await()
    }

    override suspend fun sendConversation(conversationDTO: ConversationDTO) {
        val snapshot = collection.document(conversationDTO.id)
            .get()
            .await()

        if (!snapshot.exists()) {
            collection.document(conversationDTO.id)
                .set(conversationDTO)
                .await()
        }

    }

    override suspend fun getConversationUserIdsWithOwner(ownerId: String): List<String> {
        val conversationDTO = collection.whereArrayContains("memberIds", ownerId)
            .get()
            .await()
            .toObjects<ConversationDTO>()
        Timber.d("$conversationDTO")

        val userIds = conversationDTO.mapNotNull { conversationDTO ->
            conversationDTO.memberIds.firstOrNull { uid -> uid != ownerId }
        }

        Timber.d("$userIds")

        return userIds
    }

    override suspend fun fetchConversationsByUser(uid: String): List<ConversationDTO> {
        val snapshot = collection.whereArrayContains("memberIds", uid)
            .get()
            .await()

        return snapshot.toObjects<ConversationDTO>()

    }

    override suspend fun fetchConversationById(conversationId: String): ConversationDTO? {
        return collection.document(conversationId)
            .get()
            .await()
            .toObject<ConversationDTO>()

    }

    override suspend fun fetchConversationByIdOrThrow(conversationId: String): ConversationDTO {
        return collection.document(conversationId)
            .get()
            .await()
            .toObject<ConversationDTO>() ?: throw Exception("Conversation not found")

    }

    override suspend fun sendConversations(conversationDTOList: List<ConversationDTO>) {
        optimizedWrite(
            items = conversationDTOList,
            singleWrite = { conversationDTO ->
                collection.document(conversationDTO.id)
                    .set(conversationDTO)
                    .await()
            },
            batchWrite = { list ->
                firestore.runBatch { batch ->
                    for (conversation in list) {
                        val ref = collection.document(conversation.id)
                        batch.set(ref, conversation)
                    }
                }
            }
        )
    }

    override suspend fun updateNicknameForUser(
        uid: String,
        nickName: String,
        conversationDTO: ConversationDTO
    ) {
        collection.document(conversationDTO.id)
            .update("nickName.$uid", nickName)
            .await()
    }

    override suspend fun updateConversationDataMap(dataMaps: List<Map<String, Any>>) {
        optimizedWrite(
            items = dataMaps,
            singleWrite = { singleData ->
                val uid = singleData["id"] as String? ?: return@optimizedWrite

                //remove key "id" in map
                val patch = singleData - "id"

                collection.document(uid).update(patch).await()
            },
            batchWrite = { items ->
                firestore.runBatch { batch ->
                    for (data in items) {
                        val uid = data["id"] as String? ?: continue
                        val patch = data - "id"
                        val docRef = collection.document(uid)
                        batch.update(docRef, patch)
                    }
                }.await()
            },
            batchSize = 400
        )
    }

    override suspend fun updateLastMessage(message: MessageDTO, conversation: ConversationDTO) {
        val data = mapOf(
            "lastMessageContent" to message.content,
            "lastMessageTime" to (message.sendAt ?: System.currentTimeMillis()),
            "lastSenderId" to message.senderId,
            "lastMessageType" to message.type.name,
            "unReadMessages.${message.receiverId}" to FieldValue.increment(1)
        )

        collection.document(message.conversationId)
            .update(data)
            .await()
    }

    override suspend fun updateSeedColor(conversationId: String, seedColor: Long) {
        val documentRef = collection.document(conversationId)
        documentRef.updateFieldValue("seedColor", seedColor)
    }

    override suspend fun updateMutedStatus(conversationId: String, uid: String, muted: Boolean) {
        val field = FieldPath.of("isMuted", uid)
        collection.document(conversationId)
            .update(field, muted)
            .await()

    }

    override suspend fun updatePinnedStatus(conversationId: String, uid: String, pinned: Boolean) {
        val field = FieldPath.of("isPinned", uid)
        collection.document(conversationId)
            .update(field, pinned)
            .await()
    }

    override fun streamConversationsByOwner(ownerId: String): Flow<List<ConversationDTO>> {
        return callbackFlow {
            val listener = collection
                .whereArrayContains("memberIds", ownerId)
                .orderBy("lastMessageTime", Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Timber.e(error, "Error streaming conversations")
                        trySend(emptyList())
                        return@addSnapshotListener
                    }

                    val conversationList = value?.toObjects<ConversationDTO>() ?: emptyList()
                    trySend(conversationList)
                }
            awaitClose { listener.remove() }
        }
    }

    override fun listenConversationChangesByOwner(ownerId: String): Flow<List<DataChange<ConversationDTO>>> {
        // Sửa: Thêm orderBy để đảm bảo thứ tự đồng bộ chính xác
        val query = collection
            .whereArrayContains("memberIds", ownerId)
            .orderBy("lastMessageTime", Query.Direction.DESCENDING)
        return query.observeDataChange<ConversationDTO>()
    }

    override fun listenConversationChanges(
        ownerId: String,
        limit: Int
    ): Flow<List<DataChange<ConversationDTO>>> {
        val query = collection
            .whereArrayContains("memberIds", ownerId)
            .orderBy("lastMessageTime", Query.Direction.DESCENDING)
            .limit(limit.toLong())
        return query.observeDataChange<ConversationDTO>()
    }

}
