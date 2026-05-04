package com.nhuhuy.replee.core.network.data_source.message

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObjects
import com.nhuhuy.replee.core.network.model.Constant
import com.nhuhuy.replee.core.network.model.Constant.Firestore.MESSAGE_SUBCOLLECTION
import com.nhuhuy.replee.core.network.model.DataChange
import com.nhuhuy.replee.core.network.model.MessageDTO
import com.nhuhuy.replee.core.network.model.observeMultipleDataChanges
import com.nhuhuy.replee.core.network.utils.optimizedWrite
import com.nhuhuy.replee.core.network.utils.toMilliseconds
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

interface MessageNetworkDataSource {
    suspend fun fetchPinnedMessageBefore(
        currentUserId: String,
        conversationId: String,
        lastMessageId: String,
        limit: Int,
        afterTimestamp: Long?
    ): List<MessageDTO>

    suspend fun fetchMessageByQuery(
        currentUserId: String,
        conversationId: String,
        lastMessageId: String,
        limit: Int,
        afterTimestamp: Long?,
        query: String
    ): List<MessageDTO>

    suspend fun fetchMessagesInConversationByTimestamp(
        conversationId: String,
        timestamp: Long
    ): List<MessageDTO>

    suspend fun sendMessages(list: List<MessageDTO>): List<String>
    suspend fun fetchMessagesByConversationId(conversationId: String): List<MessageDTO>
    fun listenMessageChangesByConversationId(conversationId: String): Flow<List<DataChange<MessageDTO>>>
}

class MessageNetworkDataSourceImp @Inject constructor(
    private val firestore: FirebaseFirestore
) : MessageNetworkDataSource {
    private val collection = firestore.collection(Constant.Firestore.CONVERSATION_COLLECTION)

    override suspend fun fetchMessageByQuery(
        currentUserId: String,
        conversationId: String,
        lastMessageId: String,
        limit: Int,
        afterTimestamp: Long?,
        query: String
    ): List<MessageDTO> {
        var firestoreQuery: Query = collection.document(conversationId)
            .collection(MESSAGE_SUBCOLLECTION)

        firestoreQuery = firestoreQuery
            .orderBy("content")
            .orderBy("sendAt", Query.Direction.DESCENDING)
            .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING)
            .startAt(query)
            .endAt(query + "\uf8ff")
            .limit(limit.toLong())

        if (lastMessageId.isNotEmpty()) {
            val lastDocument = collection.document(conversationId)
                .collection(MESSAGE_SUBCOLLECTION)
                .document(lastMessageId)
                .get()
                .await()

            if (lastDocument.exists()) {
                firestoreQuery = firestoreQuery.startAfter(lastDocument)
            }
        }

        val snapshot = firestoreQuery.get().await()
        val messages = snapshot.toObjects<MessageDTO>()

        return if (afterTimestamp != null) {
            messages.filter { (it.sendAt?.toMilliseconds() ?: 0L) > afterTimestamp }
        } else {
            messages
        }
    }

    override suspend fun fetchPinnedMessageBefore(
        currentUserId: String,
        conversationId: String,
        lastMessageId: String,
        limit: Int,
        afterTimestamp: Long?
    ): List<MessageDTO> {
        var query = collection.document(conversationId)
            .collection(MESSAGE_SUBCOLLECTION)
            .whereEqualTo("pinned", true)
            .orderBy("sendAt", Query.Direction.DESCENDING)
            .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING)
            .limit(limit.toLong())

        if (lastMessageId.isNotEmpty()) {
            val lastDocument = collection.document(conversationId)
                .collection(MESSAGE_SUBCOLLECTION)
                .document(lastMessageId)
                .get()
                .await()
            query = query.startAfter(lastDocument)
        }

        if (afterTimestamp != null) {
            query = query.whereGreaterThan("sendAt", Timestamp(Date(afterTimestamp)))
        }

        val snapshot = query
            .get()
            .await()

        return snapshot.toObjects<MessageDTO>()
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
                    .collection(MESSAGE_SUBCOLLECTION)
                    .document(message.messageId)

                ref.set(message).await()
                conversationIds.add(message.conversationId)
            },

            batchWrite = { messages ->
                firestore.runBatch { batch ->
                    messages.forEach { message ->
                        val ref = collection
                            .document(message.conversationId)
                            .collection(MESSAGE_SUBCOLLECTION)
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
            .collection(MESSAGE_SUBCOLLECTION)
            .get()
            .await()

        return snapshot.toObjects<MessageDTO>()
    }

    override fun listenMessageChangesByConversationId(conversationId: String): Flow<List<DataChange<MessageDTO>>> {
        val query = collection.document(conversationId)
            .collection(MESSAGE_SUBCOLLECTION)
            .orderBy("sendAt", Query.Direction.DESCENDING)
            .limit(30)
        return query.observeMultipleDataChanges()
    }


    override suspend fun fetchMessagesInConversationByTimestamp(
        conversationId: String,
        timestamp: Long
    ): List<MessageDTO> {
        return collection.document(conversationId)
            .collection(MESSAGE_SUBCOLLECTION)
            .whereGreaterThan("sendAt", Timestamp(Date(timestamp)))
            .orderBy("sendAt", Query.Direction.ASCENDING)
            .limit(20)
            .get()
            .await()
            .toObjects<MessageDTO>()
    }

}
