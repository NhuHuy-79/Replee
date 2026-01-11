package com.nhuhuy.replee.feature_chat.data.source.conversation

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.nhuhuy.replee.core.common.data.model.Account
import com.nhuhuy.replee.core.firebase.Constant
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.toRemoteFailure
import com.nhuhuy.replee.feature_chat.data.model.ConversationDTO
import com.nhuhuy.replee.feature_chat.data.model.ConversationDTOUser
import com.nhuhuy.replee.feature_chat.data.model.MessageDTO
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
        val msg : String = "Cannot parse object to class Conversation"
    ) : Exception(msg)
    private val separator = "_"
    private val collection = firestore.collection(Constant.Firestore.CONVERSATION_COLLECTION)

    private fun generateConversationId(uid1: String, uid2: String): String {
        return listOf(uid1, uid2).sorted().joinToString(separator = separator)
    }

    suspend fun updateUnReadMessageCount(conversationId: String, receiverField: String, count: Int){
        collection.document(conversationId)
            .update("unreadMessageCount.$receiverField", count)
            .await()
    }

    suspend fun getConversationById(conversationId: String) : ConversationDTO{
        return collection.document(conversationId)
            .get()
            .await()
            .toObject<ConversationDTO>() ?: throw ConversationNotFoundException()

    }

    suspend fun getOrCreateConversation(user1: Account, user2: Account) : String{
        val conversationId = generateConversationId(user1.id, user2.id)
        val firstUser = ConversationDTOUser(
            uid = user1.id,
            name = user1.name
        )

        val secondUser = ConversationDTOUser(
            uid = user2.id,
            name = user2.name
        )
        val conversationDTO = ConversationDTO(
            id = conversationId,
            user1 = firstUser,
            user2 = secondUser,
            memberIds = listOf(firstUser.uid, secondUser.uid)
        )

        val snapshot = collection.document(conversationId)
            .get()
            .await()

        if (!snapshot.exists()) {
            collection.document(conversationId)
                .set(conversationDTO)
                .await()
        }

        return  conversationId
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

    fun observeConversationList(uid: String) : Flow<Resource<List<ConversationDTO>, RemoteFailure>> = callbackFlow{
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