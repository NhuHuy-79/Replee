package com.nhuhuy.replee.feature_chat.data.source.message

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.nhuhuy.replee.core.network.model.Constant
import com.nhuhuy.replee.core.network.model.DataChange
import com.nhuhuy.replee.core.network.model.observeDataChange
import com.nhuhuy.replee.core.network.utils.optimizedWrite
import com.nhuhuy.replee.feature_chat.data.model.network.MessageDTO
import com.nhuhuy.replee.feature_chat.domain.model.MessageStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

interface MessageNetworkDataSource {
    suspend fun sendMessage(message: MessageDTO)
    suspend fun sendMessages(list: List<MessageDTO>): List<String>
    suspend fun fetchMessagesByConversationId(conversationId: String): List<MessageDTO>
    suspend fun deleteMessage(conversationId: String, messageId: String)
    suspend fun updateMessageStatus(
        receiverId: String,
        conversationId: String,
        messageIds: List<String>,
        status: MessageStatus,
    ): Int

    suspend fun updateMessageSeenStatus(
        conversationId: String,
        messageIds: List<String>,
        receiverId: String
    ): Int

    fun listenMessageChangesByConversationId(conversationId: String): Flow<List<DataChange<MessageDTO>>>
    fun listenToMessagesWithLimit(
        conversationId: String,
        limit: Int = 3
    ): Flow<List<DataChange<MessageDTO>>>

    suspend fun fetchMessagesPage(
        conversationId: String,
        limit: Int,
        startAfterCreatedAt: Long?,
        startAfterMessageId: String?
    ): List<MessageDTO>

    fun streamMessageListByConversationId(conversationId: String): Flow<List<MessageDTO>>
}

class MessageNetworkDataSourceImp @Inject constructor(
    private val firestore: FirebaseFirestore
) : MessageNetworkDataSource {
    private val collection = firestore.collection(Constant.Firestore.CONVERSATION_COLLECTION)

    override suspend fun sendMessage(message: MessageDTO) {
        val data = mapOf(
            "lastMessageTime" to message.sendAt,
            "lastMessageContent" to message.content,
            "lastSenderId" to message.senderId,
            "lastMessageType" to message.type

        )
        collection.document(message.conversationId).apply {
            update(data).await()
            collection(Constant.Firestore.MESSAGE_SUBCOLLECTION)
                .document(message.messageId)
                .set(message)
                .await()
        }
    }

    override suspend fun sendMessages(
        list: List<MessageDTO>
    ): List<String> {

        if (list.isEmpty()) return emptyList()

        val conversationIds = mutableSetOf<String>()

        optimizedWrite(
            items = list,

            singleWrite = { message ->
                val ref = collection
                    .document(message.conversationId)
                    .collection(Constant.Firestore.MESSAGE_SUBCOLLECTION)
                    .document(message.messageId)

                ref.set(message).await()
                conversationIds.add(message.conversationId)
            },

            batchWrite = { messages ->
                firestore.runBatch { batch ->
                    messages.forEach { message ->
                        val ref = collection
                            .document(message.conversationId)
                            .collection(Constant.Firestore.MESSAGE_SUBCOLLECTION)
                            .document(message.messageId)

                        batch.set(ref, message, SetOptions.merge())
                        conversationIds.add(message.conversationId)
                    }
                }.await()
            },

            batchSize = 400
        )

        return conversationIds.toList()
    }

    override suspend fun fetchMessagesByConversationId(conversationId: String): List<MessageDTO> {
        val snapshot = collection.document(conversationId)
            .collection(Constant.Firestore.MESSAGE_SUBCOLLECTION)
            .get()
            .await()

        return snapshot.toObjects<MessageDTO>()
    }

    override suspend fun deleteMessage(conversationId: String, messageId: String) {
        collection.document(conversationId)
            .collection(Constant.Firestore.MESSAGE_SUBCOLLECTION)
            .document(messageId)
            .delete()
            .await()
    }

    override suspend fun updateMessageStatus(
        receiverId: String,
        conversationId: String,
        messageIds: List<String>,
        status: MessageStatus,
    ): Int {
        if (messageIds.isEmpty()) return 0

        val messageCollection = collection.document(conversationId)
            .collection(Constant.Firestore.MESSAGE_SUBCOLLECTION)

        val refs = messageIds.map { msgId ->
            messageCollection.document(msgId)
        }

        optimizedWrite(
            items = refs,
            singleWrite = { reference ->
                val data = mapOf("status" to status.name)
                reference.set(data, SetOptions.merge()).await()
            },
            batchWrite = { chunkRefs ->
                firestore.runBatch { batch ->
                    for (ref in chunkRefs) {
                        val data = mapOf("status" to status.name)
                        batch.set(ref, data, SetOptions.merge())
                    }
                }.await()
            },
        )

        return refs.size
    }

    override suspend fun updateMessageSeenStatus(
        conversationId: String,
        messageIds: List<String>,
        receiverId: String
    ): Int {
        if (messageIds.isEmpty()) return 0
        Timber.d("messages: $messageIds")

        val messageCollection = firestore
            .collection(Constant.Firestore.CONVERSATION_COLLECTION)
            .document(conversationId)
            .collection(Constant.Firestore.MESSAGE_SUBCOLLECTION)

        val updateData = mapOf("seen" to true)
        val chunks = messageIds.distinct().chunked(500)
        chunks.forEach { chunkIds ->
            firestore.runBatch { batch ->
                chunkIds.forEach { msgId ->
                    val docRef = messageCollection.document(msgId)
                    batch.set(docRef, updateData, SetOptions.merge())
                }
            }.await()
        }
        return messageIds.size
    }

    override fun listenMessageChangesByConversationId(conversationId: String): Flow<List<DataChange<MessageDTO>>> {
        val query = collection.document(conversationId)
            .collection(Constant.Firestore.MESSAGE_SUBCOLLECTION)
            .orderBy("sendAt", Query.Direction.DESCENDING)
        return query.observeDataChange()
    }

    override fun listenToMessagesWithLimit(
        conversationId: String,
        limit: Int
    ): Flow<List<DataChange<MessageDTO>>> {
        val query = collection.document(conversationId)
            .collection(Constant.Firestore.MESSAGE_SUBCOLLECTION)
            .orderBy("sendAt", Query.Direction.DESCENDING)
            .limit(limit.toLong())
        return query.observeDataChange()
    }

    override suspend fun fetchMessagesPage(
        conversationId: String,
        limit: Int,
        startAfterCreatedAt: Long?,
        startAfterMessageId: String?
    ): List<MessageDTO> {

        val baseQuery = collection.document(conversationId)
            .collection(Constant.Firestore.MESSAGE_SUBCOLLECTION)
            .orderBy("sendAt", Query.Direction.DESCENDING)
            .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING)
            .limit(limit.toLong())

        val finalQuery = if (startAfterCreatedAt != null && startAfterMessageId != null) {
            baseQuery.startAfter(startAfterCreatedAt, startAfterMessageId)
        } else {
            baseQuery
        }

        val snapshot = finalQuery.get().await()

        return snapshot.documents.mapNotNull { doc ->
            doc.toObject<MessageDTO>()
        }
    }

    override fun streamMessageListByConversationId(conversationId: String): Flow<List<MessageDTO>> {
        return callbackFlow {
            val listener = collection
                .document(conversationId)
                .collection(Constant.Firestore.MESSAGE_SUBCOLLECTION)
                .orderBy("sendAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(emptyList())
                        close()
                    }
                    val messages = snapshot?.toObjects<MessageDTO>() ?: emptyList()
                    trySend(messages)
                }

            awaitClose { listener.remove() }
        }
    }

}
