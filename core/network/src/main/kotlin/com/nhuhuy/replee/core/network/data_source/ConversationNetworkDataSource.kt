package com.nhuhuy.replee.core.network.data_source

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.nhuhuy.replee.core.network.model.Constant
import com.nhuhuy.replee.core.network.model.ConversationDTO
import com.nhuhuy.replee.core.network.model.DataChange
import com.nhuhuy.replee.core.network.model.MessageDTO
import com.nhuhuy.replee.core.network.model.observeMultipleDataChanges
import com.nhuhuy.replee.core.network.utils.optimizedWrite
import kotlinx.coroutines.flow.Flow
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
    suspend fun updateNicknameForUser(
        uid: String,
        nickName: String,
        conversationDTO: ConversationDTO,
    )

    suspend fun updateConversationDataMap(dataMaps: List<Map<String, Any>>)
    suspend fun updateLastMessage(message: MessageDTO, conversation: ConversationDTO)
    suspend fun updateMutedStatus(conversationId: String, uid: String, muted: Boolean)
    suspend fun updatePinnedStatus(conversationId: String, uid: String, pinned: Boolean)
    fun listenConversationChanges(
        ownerId: String,
        limit: Int
    ): Flow<List<DataChange<ConversationDTO>>>

    suspend fun updateIsDeletedMultiConversations(dataMaps: List<Map<String, Any>>)

}

class ConversationNetworkDataSourceImp @Inject constructor(
    private val firestore: FirebaseFirestore,
) : ConversationNetworkDataSource {
    private val collection = firestore.collection(Constant.Firestore.CONVERSATION_COLLECTION)
    override suspend fun deleteAllUnreadMessages(
        conversationId: String,
        uid: String
    ) {
        val field = mapOf("unReadMessages.$uid" to 0)
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
        Timber.d(conversationDTO.toString())

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

    override suspend fun updateNicknameForUser(
        uid: String,
        nickName: String,
        conversationDTO: ConversationDTO,
    ) {
        collection.document(conversationDTO.id)
            .update("nickName.$uid", nickName)
            .await()
    }

    override suspend fun updateConversationDataMap(dataMaps: List<Map<String, Any>>) {
        optimizedWrite(
            items = dataMaps,
            singleWrite = { singleData ->
                val uid = (singleData["id"] as? String) ?: return@optimizedWrite

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
        val messageDTO = collection.document(conversation.id)
            .collection(Constant.Firestore.MESSAGE_SUBCOLLECTION)
            .document(message.messageId)
            .get()
            .await()
            .toObject<MessageDTO>() ?: message

        val data = mapOf(
            "lastMessageId" to messageDTO.messageId,
            "lastMessageContent" to messageDTO.content,
            "lastMessageTime" to (messageDTO.sendAt ?: Timestamp.now()),
            "lastSenderId" to messageDTO.senderId,
            "lastMessageType" to messageDTO.type.name,
            "unReadMessages.${messageDTO.receiverId}" to FieldValue.increment(1)
        )

        collection.document(message.conversationId)
            .update(data)
            .await()
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

    override fun listenConversationChanges(
        ownerId: String,
        limit: Int
    ): Flow<List<DataChange<ConversationDTO>>> {
        val query = collection
            .whereArrayContains("memberIds", ownerId)
            .orderBy("lastMessageTime", Query.Direction.DESCENDING)
            .limit(limit.toLong())
        return query.observeMultipleDataChanges<ConversationDTO>()
    }

    override suspend fun updateIsDeletedMultiConversations(dataMaps: List<Map<String, Any>>) {
        updateConversationDataMap(dataMaps)
    }

}
