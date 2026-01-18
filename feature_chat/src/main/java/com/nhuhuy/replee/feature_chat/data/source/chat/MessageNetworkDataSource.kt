package com.nhuhuy.replee.feature_chat.data.source.chat

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObjects
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.toRemoteFailure
import com.nhuhuy.replee.core.firebase.data.Constant
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

    suspend fun sendMessage(message: MessageDTO, conversationId: String) {
        val data = mapOf(
            "lastMessageTime" to message.sendAt,
            "lastMessageContent" to message.content,
            "lastSenderId" to message.senderId
        )
        collection.document(conversationId).apply {
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

        Timber.d("snapshots size: ${snapshots.size()}")


        firestore.runBatch { batch ->
            snapshots.forEach { snapshot ->
                val messageRef = snapshot.reference
                batch.update(messageRef, "seen", true)
            }
        }.await()

        return snapshots.size()
    }

    fun streamMessagesByConversationId(conversationId: String): Flow<Resource<List<MessageDTO>, RemoteFailure>>{
        return callbackFlow {
            val listener = collection
                .document(conversationId)
                .collection(Constant.Firestore.MESSAGE_SUBCOLLECTION)
                .orderBy("sendAt", Query.Direction.DESCENDING)
                .whereEqualTo("conversationId", conversationId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null){
                        trySend(Resource.Error(error.toRemoteFailure()))
                        close()
                    }
                    if (snapshot == null){
                        trySend(Resource.Success(emptyList()))
                    } else {
                        val messageList = snapshot.toObjects<MessageDTO>()
                        trySend(Resource.Success(messageList))
                    }
                }

            awaitClose { listener.remove() }
        }
    }
}