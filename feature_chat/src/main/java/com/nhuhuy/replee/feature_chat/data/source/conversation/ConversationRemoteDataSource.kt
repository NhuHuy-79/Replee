package com.nhuhuy.replee.feature_chat.data.source.conversation

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import com.nhuhuy.replee.core.common.data.Constant
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.firebase.utils.toRemoteFailure
import com.nhuhuy.replee.feature_chat.data.model.ConversationDTO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class ConversationRemoteDataSource @Inject constructor(
    firestore: FirebaseFirestore,
) {

    private val collection = firestore.collection(Constant.Firestore.CONVERSATION_COLLECTION)

    suspend fun addConversation(conversation: ConversationDTO) {
        collection.document(conversation.id)
            .set(conversation)
            .await()
    }

    suspend fun fetchOtherUserInConversation(uid: String){
        collection.whereArrayContains("membersId", uid)
    }

    fun observeConversationList(uid: String) : Flow<Resource<List<ConversationDTO>, RemoteFailure>> = callbackFlow{
        val listener = collection
            .whereArrayContains("membersId", uid)
            .addSnapshotListener { value, error ->
            if (error != null) {
                Timber.e(error)
                trySend(
                    Resource.Error((error as Exception).toRemoteFailure())
                )
                return@addSnapshotListener
            }
            val conversationList = value?.toObjects<ConversationDTO>()?: emptyList()
            trySend(
                Resource.Success(conversationList)
            )
        }

        awaitClose { listener.remove() }
    }
}