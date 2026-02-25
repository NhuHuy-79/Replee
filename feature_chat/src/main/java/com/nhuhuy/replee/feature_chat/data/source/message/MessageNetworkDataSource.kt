package com.nhuhuy.replee.feature_chat.data.source.message

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.nhuhuy.replee.core.firebase.model.Constant
import com.nhuhuy.replee.core.firebase.model.DataChange
import com.nhuhuy.replee.core.firebase.model.observeDataChange
import com.nhuhuy.replee.core.firebase.utils.optimizedWrite
import com.nhuhuy.replee.feature_chat.data.model.network.MessageDTO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class
MessageNetworkDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
){
    private val collection = firestore.collection(Constant.Firestore.CONVERSATION_COLLECTION)

    suspend fun sendMessage(message: MessageDTO) {
        val data = mapOf(
            "lastMessageTime" to message.sendAt,
            "lastMessageContent" to message.content,
            "lastSenderId" to message.senderId
        )
        collection.document(message.conversationId).apply {
            update(data).await()
            collection(Constant.Firestore.MESSAGE_SUBCOLLECTION)
                .document(message.messageId)
                .set(message).await()
        }
    }

    suspend fun sendMessages(list: List<MessageDTO>) : List<String>{
        val messageMap: Map<String, List<MessageDTO>> = list.groupBy { messageDTO -> messageDTO.conversationId }
        val conversationIds: MutableList<String> = mutableListOf()
        firestore.runBatch { batch ->
            messageMap.forEach { conversationId, messages ->
                for (message in messages) {
                    val ref = collection.document(conversationId)
                        .collection(Constant.Firestore.MESSAGE_SUBCOLLECTION)
                        .document(message.messageId)
                    batch.set(ref, message)
                    conversationIds.add(conversationId)
                }
            }
        }.await()

        return conversationIds
    }

    suspend fun fetchMessagesByConversationId(conversationId: String) : List<MessageDTO>{
        val snapshot = collection.document(conversationId)
            .collection(Constant.Firestore.MESSAGE_SUBCOLLECTION)
            .get()
            .await()

        return snapshot.toObjects<MessageDTO>()
    }

    suspend fun deleteMessage(conversationId: String, messageId: String){
        collection.document(conversationId)
            .collection(Constant.Firestore.MESSAGE_SUBCOLLECTION)
            .document(messageId)
            .delete()
            .await()
    }

    suspend fun updateMessageSeenStatus(conversationId: String, messageIds: List<String>, receiverId: String) : Int{
        if (messageIds.isEmpty()) return 0

        Timber.d("messages: $messageIds")

        val snapshots = firestore
            .collection(Constant.Firestore.CONVERSATION_COLLECTION)
            .document(conversationId)
            .collection(Constant.Firestore.MESSAGE_SUBCOLLECTION)
            .whereEqualTo("receiverId", receiverId)
            .whereIn(FieldPath.documentId(), messageIds)
            .whereEqualTo("seen", false)
            .get()
            .await()

        val refs = snapshots.map { snapshot -> snapshot.reference }
        optimizedWrite(
            items = refs,
            singleWrite = { reference ->
                reference.update("seen", true)
            },
            batchWrite = {
                firestore.runBatch { batch ->
                    snapshots.forEach { snapshot ->
                        val messageRef = snapshot.reference
                        batch.update(messageRef, "seen", true)
                    }
                }.await()
            }
        )

        Timber.d("snapshots size: ${snapshots.size()}")

        return snapshots.size()
    }

    fun listenMessageChangesByConversationId(conversationId: String): Flow<List<DataChange<MessageDTO>>> {
        val query = collection.document(conversationId)
            .collection(Constant.Firestore.MESSAGE_SUBCOLLECTION)
            .orderBy("sendAt", Query.Direction.DESCENDING)
        return query.observeDataChange()
    }

    fun listenToMessagesWithLimit(
        conversationId: String,
        limit: Int = 3
    ): Flow<List<DataChange<MessageDTO>>> {
        val query = collection.document(conversationId)
            .collection(Constant.Firestore.MESSAGE_SUBCOLLECTION)
            .orderBy("sendAt", Query.Direction.DESCENDING)
            .limit(limit.toLong())
        return query.observeDataChange()
    }

    suspend fun fetchMessagesPage(
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

    fun streamMessageListByConversationId(conversationId: String): Flow<List<MessageDTO>> {
        return callbackFlow {
            val listener = collection
                .document(conversationId)
                .collection(Constant.Firestore.MESSAGE_SUBCOLLECTION)
                .orderBy("sendAt", Query.Direction.DESCENDING)
                .whereEqualTo("conversationId", conversationId)
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