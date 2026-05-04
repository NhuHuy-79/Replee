package com.nhuhuy.replee.core.network.data_source.transaction

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.nhuhuy.replee.core.network.model.Constant
import com.nhuhuy.replee.core.network.model.MessageDTO
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


interface NetworkTransactionRunner {
    suspend fun sendMessageAndUpdateConversation(messageDTO: MessageDTO)
    suspend fun pinMessageAndUpdateConversation(
        messageId: String,
        pinned: Boolean,
        conversationId: String,
    )

    suspend fun addReactToMessageAndUpdateConversation(
        userId: String,
        reaction: String,
        messageId: String,
        conversationId: String
    )

    suspend fun removeReactToMessageAndUpdateConversation(
        userId: String,
        reaction: String,
        messageId: String,
        conversationId: String
    )

    suspend fun editMessageAndUpdateConversation(messageDTO: MessageDTO)
    suspend fun deleteMessageAndUpdateConversation(messageDTO: MessageDTO)
}


class NetworkTransactionRunnerImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : NetworkTransactionRunner {
    private val conversationCollection =
        firestore.collection(Constant.Firestore.CONVERSATION_COLLECTION)

    override suspend fun sendMessageAndUpdateConversation(messageDTO: MessageDTO) {
        val conversationRef = conversationCollection.document(messageDTO.conversationId)
        val messageRef = conversationRef.collection(Constant.Firestore.CONVERSATION_COLLECTION)
            .document(messageDTO.messageId)
        firestore.runBatch { batch ->
            batch.set(messageRef, messageDTO)
            batch.update(
                conversationRef, mapOf(
                    "lastMessageTime" to messageDTO.sendAt,
                    "lastMessageContent" to messageDTO.content,
                    "lastSenderId" to messageDTO.senderId,
                    "lastMessageType" to messageDTO.type,
                    "lastSync" to FieldValue.serverTimestamp()
                )
            )
        }.await()
    }

    override suspend fun pinMessageAndUpdateConversation(
        messageId: String,
        pinned: Boolean,
        conversationId: String,
    ) {
        val conversationRef = conversationCollection.document(conversationId)
        val messageRef = conversationRef.collection(Constant.Firestore.CONVERSATION_COLLECTION)
            .document(messageId)
        firestore.runBatch { batch ->
            batch.set(messageRef, mapOf("pinned" to pinned), SetOptions.merge())
            batch.update(conversationRef, "lastSync", FieldValue.serverTimestamp())
        }.await()
    }

    override suspend fun addReactToMessageAndUpdateConversation(
        userId: String,
        reaction: String,
        messageId: String,
        conversationId: String
    ) {
        val conversationRef = conversationCollection.document(conversationId)
        val messageRef = conversationRef.collection(Constant.Firestore.CONVERSATION_COLLECTION)
            .document(messageId)
        firestore.runBatch { batch ->
            batch.update(messageRef, "reactions.$userId", FieldValue.arrayUnion(reaction))
            batch.update(conversationRef, "lastSync", FieldValue.serverTimestamp())
        }.await()
    }

    override suspend fun removeReactToMessageAndUpdateConversation(
        userId: String,
        reaction: String,
        messageId: String,
        conversationId: String
    ) {
        val conversationRef = conversationCollection.document(conversationId)
        val messageRef = conversationRef.collection(Constant.Firestore.CONVERSATION_COLLECTION)
            .document(messageId)
        firestore.runBatch { batch ->
            batch.update(messageRef, "reactions.$userId", FieldValue.arrayRemove(reaction))
            batch.update(conversationRef, "lastSync", FieldValue.serverTimestamp())
        }.await()
    }

    override suspend fun editMessageAndUpdateConversation(messageDTO: MessageDTO) {
        val conversationRef = conversationCollection.document(messageDTO.conversationId)
        val messageRef = conversationRef.collection(Constant.Firestore.CONVERSATION_COLLECTION)
            .document(messageDTO.messageId)
        firestore.runTransaction { batch ->
            batch.update(
                messageRef, mapOf(
                    "content" to messageDTO.content,
                    "edited" to true
                )
            )
            batch.update(conversationRef, "lastSync", FieldValue.serverTimestamp())
        }.await()
    }

    override suspend fun deleteMessageAndUpdateConversation(messageDTO: MessageDTO) {
        val conversationRef = conversationCollection.document(messageDTO.conversationId)
        val messageRef = conversationRef.collection(Constant.Firestore.CONVERSATION_COLLECTION)
            .document(messageDTO.messageId)
        firestore.runTransaction { batch ->
            batch.delete(messageRef)
            batch.update(
                conversationRef, mapOf(
                    "lastDeletedMessageId" to messageDTO.messageId,
                    "lastSync" to FieldValue.serverTimestamp()
                )
            )
        }.await()
    }
}