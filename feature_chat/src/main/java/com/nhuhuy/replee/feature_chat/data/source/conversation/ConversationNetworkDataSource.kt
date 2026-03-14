package com.nhuhuy.replee.feature_chat.data.source.conversation

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
import com.nhuhuy.replee.feature_chat.data.model.network.ConversationPatch
import com.nhuhuy.replee.feature_chat.data.model.network.MessageDTO
import com.nhuhuy.replee.feature_chat.utils.removeFieldValueInArray
import com.nhuhuy.replee.feature_chat.utils.unionFieldValueInArray
import com.nhuhuy.replee.feature_chat.utils.updateFieldValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

interface ConversationNetworkDataSource {
    suspend fun updateUnreadMessageCount(conversationId: String, receiverField: String, count: Int)
    suspend fun sendConversation(conversationDTO: ConversationDTO)
    suspend fun getConversationUserIdsWithOwner(ownerId: String): List<String>
    suspend fun fetchConversationsByUser(uid: String): List<ConversationDTO>
    suspend fun fetchConversationById(conversationId: String): ConversationDTO?
    suspend fun fetchConversationByIdOrThrow(conversationId: String): ConversationDTO
    suspend fun sendConversations(conversationDTOList: List<ConversationDTO>)
    suspend fun updateConversations(conversationPatchList: List<ConversationPatch>)
    suspend fun updateNicknameForUser(
        uid: String,
        nickName: String,
        conversationDTO: ConversationDTO
    )

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

    override suspend fun updateUnreadMessageCount(
        conversationId: String,
        receiverField: String,
        count: Int
    ) {
        collection.document(conversationId)
            .update("unreadMessageCount.$receiverField", count)
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
        val userIds = conversationDTO.map { conversationDTO ->
            if (conversationDTO.user1.uid == ownerId) {
                conversationDTO.user2.uid
            } else {
                conversationDTO.user1.uid
            }
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
        //Optimize for write multi conversationDTOs to firestore
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

    override suspend fun updateConversations(conversationPatchList: List<ConversationPatch>) {
        firestore.runBatch { batch ->
            for (conversation in conversationPatchList) {
                val ref = collection.document(conversation.id)
                batch.update(ref, conversation.mapFieldValue)
                    .update(ref, conversation.mapLastMessage)
            }
        }.await()

    }

    override suspend fun updateNicknameForUser(
        uid: String,
        nickName: String,
        conversationDTO: ConversationDTO
    ) {
        collection.document(conversationDTO.id)
        val userKey = if (uid == conversationDTO.user1.uid) "user1" else "user2"
        val mapData = mapOf(
            "$userKey.nick" to nickName
        )
        Timber.tag("ChangeNickName").d("Call")
        collection.document(conversationDTO.id)
            .update(mapData)
            .await()
    }

    override suspend fun updateLastMessage(message: MessageDTO, conversation: ConversationDTO) {

        val receiverId = if (message.senderId == conversation.user1.uid) {
            "user2"
        } else {
            "user1"
        }

        val data = mapOf(
            "lastMessageContent" to message.content,
            "lastMessageTime" to message.sendAt,
            "lastSenderId" to message.senderId,
            "lastMessageType" to message.type.name,
            "unreadMessageCount.$receiverId" to FieldValue.increment(1)
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
        val documentRef = collection.document(conversationId)
        if (muted) {
            documentRef.unionFieldValueInArray("mutedBy", uid)
        } else {
            documentRef.removeFieldValueInArray("mutedBy", uid)
        }
    }

    override suspend fun updatePinnedStatus(conversationId: String, uid: String, pinned: Boolean) {
        val documentRef = collection.document(conversationId)
        if (pinned) {
            documentRef.unionFieldValueInArray("pinnedBy", uid)
        } else {
            documentRef.removeFieldValueInArray("pinnedBy", uid)
        }
    }

    override fun streamConversationsByOwner(ownerId: String): Flow<List<ConversationDTO>> {
        return callbackFlow {
            val listener = collection
                .whereArrayContains("memberIds", ownerId)
                .whereNotEqualTo("lastMessageContent", "")
                .orderBy("lastMessageContent")
                .orderBy("lastMessageTime", Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Timber.e(error)
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
        val query = collection
            .whereArrayContains("memberIds", ownerId)
        return query.observeDataChange<ConversationDTO>()
    }

    override fun listenConversationChanges(
        ownerId: String,
        limit: Int
    ): Flow<List<DataChange<ConversationDTO>>> {
        val query = collection.whereArrayContains("memberIds", ownerId)
            .limit(limit.toLong())
        return query.observeDataChange<ConversationDTO>()
    }

}