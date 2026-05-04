package com.nhuhuy.replee.core.network.data_source.transaction

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.nhuhuy.replee.core.network.model.Constant
import com.nhuhuy.replee.core.network.model.MessageDTO
import com.nhuhuy.replee.core.network.utils.multipleRunBatch
import javax.inject.Inject

interface NetworkMultipleWriteRunner {
    suspend fun deleteMessagesAndUpdateConversations(messageDTOs: List<MessageDTO>)
    suspend fun pinMessagesAndUpdateConversation(messageDTOs: List<MessageDTO>, pinned: Boolean)
    suspend fun sendMessagesAndUpdateConversation(messageDTOs: List<MessageDTO>)
    suspend fun reactToMessageAndUpdateConversation(
        messageDTOs: List<MessageDTO>,
        currentUserId: String
    )
}

class NetworkMultipleWriteRunnerImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
) : NetworkMultipleWriteRunner {
    private val chatCollection =
        firebaseFirestore.collection(Constant.Firestore.CONVERSATION_COLLECTION)

    override suspend fun deleteMessagesAndUpdateConversations(messageDTOs: List<MessageDTO>) {
        firebaseFirestore.multipleRunBatch(
            items = messageDTOs,
            block = { messageDTO, transaction ->
                val conversationRef = chatCollection.document(messageDTO.conversationId)
                val messageRef =
                    conversationRef.collection(Constant.Firestore.MESSAGE_SUBCOLLECTION)
                        .document(messageDTO.messageId)
                transaction.delete(messageRef)
                transaction.update(conversationRef, "lastSynced", FieldValue.serverTimestamp())
            },
        )
    }

    override suspend fun pinMessagesAndUpdateConversation(
        messageDTOs: List<MessageDTO>,
        pinned: Boolean
    ) {
        firebaseFirestore.multipleRunBatch(
            items = messageDTOs,
            block = { messageDTO, transaction ->
                val conversationRef = chatCollection.document(messageDTO.conversationId)
                val messageRef =
                    conversationRef.collection(Constant.Firestore.MESSAGE_SUBCOLLECTION)
                        .document(messageDTO.messageId)
                transaction.update(messageRef, "pinned", pinned)
                transaction.update(conversationRef, "lastSynced", FieldValue.serverTimestamp())
            },
        )
    }

    override suspend fun sendMessagesAndUpdateConversation(messageDTOs: List<MessageDTO>) {
        firebaseFirestore.multipleRunBatch(
            items = messageDTOs,
            block = { messageDTO, transaction ->
                val conversationRef = chatCollection.document(messageDTO.conversationId)
                val messageRef =
                    conversationRef.collection(Constant.Firestore.MESSAGE_SUBCOLLECTION)
                        .document(messageDTO.messageId)
                transaction.set(messageRef, messageDTO)
                transaction.update(
                    conversationRef, mapOf(
                        "lastMessageTime" to messageDTO.sendAt,
                        "lastMessageContent" to messageDTO.content,
                        "lastSenderId" to messageDTO.senderId,
                        "lastMessageType" to messageDTO.type,
                        "lastSynced" to FieldValue.serverTimestamp()
                    )
                )
            },
        )
    }

    override suspend fun reactToMessageAndUpdateConversation(
        messageDTOs: List<MessageDTO>,
        currentUserId: String
    ) {
        firebaseFirestore.multipleRunBatch(
            items = messageDTOs,
            block = { messageDTO, batch ->
                val conversationRef = chatCollection.document(messageDTO.conversationId)
                val messageRef =
                    conversationRef.collection(Constant.Firestore.MESSAGE_SUBCOLLECTION)
                        .document(messageDTO.messageId)
                val reaction = messageDTO.reactions[currentUserId]
                batch.update(messageRef, "reactions.$currentUserId", reaction)
                batch.update(
                    conversationRef, mapOf("lastSynced" to FieldValue.serverTimestamp())
                )
            },
        )
    }
}