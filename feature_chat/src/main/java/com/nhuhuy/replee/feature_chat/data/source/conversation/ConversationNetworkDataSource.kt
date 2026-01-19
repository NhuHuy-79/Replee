package com.nhuhuy.replee.feature_chat.data.source.conversation

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.toRemoteFailure
import com.nhuhuy.replee.core.firebase.data.Constant
import com.nhuhuy.replee.feature_chat.data.model.network.ConversationDTO
import com.nhuhuy.replee.feature_chat.data.model.network.MessageDTO
import com.nhuhuy.replee.feature_chat.utils.updateFieldValue
import com.nhuhuy.replee.feature_chat.utils.updateFieldValueInArray
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class ConversationNetworkDataSource @Inject constructor(
    firestore: FirebaseFirestore,
) {

    class ConversationNotFoundException(
        msg : String = "Cannot parse object to class Conversation"
    ) : Exception(msg)

    private val collection = firestore.collection(Constant.Firestore.CONVERSATION_COLLECTION)

    suspend fun updateUnreadMessageCount(conversationId: String, receiverField: String, count: Int){
        collection.document(conversationId)
            .update("unreadMessageCount.$receiverField", count)
            .await()
    }

    suspend fun sendConversation(conversationDTO: ConversationDTO){
        val snapshot = collection.document(conversationDTO.id)
            .get()
            .await()

        if (!snapshot.exists()) {
            collection.document(conversationDTO.id)
                .set(conversationDTO)
                .await()
        }

    }

    suspend fun fetchConversationsByUser(uid: String) : List<ConversationDTO>{
        val snapshot = collection.whereArrayContains("memberIds", uid)
            .get()
            .await()

        return snapshot.toObjects<ConversationDTO>()

    }

    suspend fun fetchConversationById(conversationId: String) : ConversationDTO{
        return collection.document(conversationId)
            .get()
            .await()
            .toObject<ConversationDTO>() ?: throw ConversationNotFoundException()

    }

    suspend fun updateLastMessage(message: MessageDTO, conversation: ConversationDTO){

        val receiverId = if (message.senderId == conversation.user1.uid) {
            "user2"
        } else {
            "user1"
        }

        val data = mapOf(
            "lastMessageContent" to message.content,
            "lastMessageTime" to message.sendAt,
            "lastSenderId" to message.senderId,
            "unreadMessageCount.$receiverId" to FieldValue.increment(1)
        )

        collection.document(message.conversationId)
            .update(data)
            .await()
    }

    suspend fun updateSeedColor(conversationId: String, seedColor: Long){
        val documentRef = collection.document(conversationId)
        documentRef.updateFieldValue("seedColor", seedColor)
    }

    suspend fun updateMutedStatus(conversationId: String, uid: String){
        val documentRef = collection.document(conversationId)
        documentRef.updateFieldValueInArray("muted", uid)
    }

    suspend fun updatePinnedStatus(conversationId: String, uid: String){
        val documentRef = collection.document(conversationId)
        documentRef.updateFieldValueInArray("pinnedBy", uid)
    }

    fun streamConversationsByUser(uid: String) : Flow<Resource<List<ConversationDTO>, RemoteFailure>> = callbackFlow{
        val listener = collection
            .whereArrayContains("memberIds", uid)
            .whereNotEqualTo("lastMessageContent", "")
            .orderBy("lastMessageContent")
            .orderBy("lastMessageTime", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Timber.e(error)
                    trySend(
                        Resource.Error((error as Exception).toRemoteFailure())
                    )
                    return@addSnapshotListener
                }

                val conversationList =
                    value?.toObjects<ConversationDTO>() ?: emptyList()

                trySend(Resource.Success(conversationList))
            }

        awaitClose { listener.remove() }

    }
}