package com.nhuhuy.replee.feature_chat.data.source.paging

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nhuhuy.replee.core.network.model.Constant
import com.nhuhuy.replee.feature_chat.data.model.network.MessageDTO
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface PagingMessageNetworkDataSource {
    suspend fun fetchInitialMessageList(
        conversationId: String,
        pageSize: Long
    ): List<MessageDTO>

    suspend fun fetchMessageListAroundAnchor(
        conversationId: String,
        messageId: String,
        pageSize: Long
    ): List<MessageDTO>

    suspend fun fetchMessageListBeforeAnchor(
        conversationId: String,
        messageId: String,
        pageSize: Long
    ): List<MessageDTO>

    suspend fun fetchMessageListAfterAnchor(
        conversationId: String,
        messageId: String,
        pageSize: Long
    ): List<MessageDTO>
}

class PagingMessageNetworkDataSourceImp @Inject constructor(
    private val firestore: FirebaseFirestore
) : PagingMessageNetworkDataSource {
    private val conversationRef = firestore.collection(Constant.Firestore.CONVERSATION_COLLECTION)
    override suspend fun fetchInitialMessageList(
        conversationId: String,
        pageSize: Long
    ): List<MessageDTO> {
        val messageRef = conversationRef.document(conversationId)
            .collection(Constant.Firestore.MESSAGE_SUBCOLLECTION)

        val initialMessageList: List<MessageDTO> =
            messageRef.orderBy("sendAt", Query.Direction.DESCENDING)
                .limit(pageSize)
                .get()
                .await()
                .toObjects(MessageDTO::class.java)

        return initialMessageList
    }

    override suspend fun fetchMessageListAroundAnchor(
        conversationId: String,
        messageId: String,
        pageSize: Long
    ): List<MessageDTO> {
        val messageRef = conversationRef.document(conversationId)
            .collection(Constant.Firestore.MESSAGE_SUBCOLLECTION)

        val anchorSnapshot = messageRef.document(messageId).get().await()

        if (!anchorSnapshot.exists()) return emptyList()

        val beforeMessageSize = pageSize / 2
        val afterMessageSize = pageSize - beforeMessageSize

        val beforeMessageList = messageRef
            .orderBy("sendAt", Query.Direction.DESCENDING)
            .startAfter(anchorSnapshot)
            .limit(beforeMessageSize)
            .get()
            .await()
            .toObjects(MessageDTO::class.java)


        val afterMessageList = messageRef
            .orderBy("sendAt", Query.Direction.ASCENDING)
            .startAt(anchorSnapshot)
            .limit(afterMessageSize)
            .get()
            .await()
            .toObjects(MessageDTO::class.java)

        val safeCombinedList = (beforeMessageList + afterMessageList)
            .distinctBy { it.messageId }
            .sortedWith(
                compareByDescending<MessageDTO> { it.sendAt }
                    .thenByDescending { it.messageId }
            )

        return safeCombinedList
    }

    override suspend fun fetchMessageListBeforeAnchor(
        conversationId: String,
        messageId: String,
        pageSize: Long
    ): List<MessageDTO> {
        val messageRef = conversationRef.document(conversationId)
            .collection(Constant.Firestore.MESSAGE_SUBCOLLECTION)
        val anchorSnapshot = messageRef.document(messageId).get().await()
        val beforeMessageList = messageRef
            .orderBy("sendAt", Query.Direction.DESCENDING)
            .startAfter(anchorSnapshot)
            .limit(pageSize)
            .get()
            .await()
            .toObjects(MessageDTO::class.java)

        return beforeMessageList
    }

    override suspend fun fetchMessageListAfterAnchor(
        conversationId: String,
        messageId: String,
        pageSize: Long
    ): List<MessageDTO> {
        val messageRef = conversationRef.document(conversationId)
            .collection(Constant.Firestore.MESSAGE_SUBCOLLECTION)
        val anchorSnapshot = messageRef.document(messageId).get().await()
        val afterMessageList = messageRef
            .orderBy("sendAt", Query.Direction.ASCENDING)
            .startAfter(anchorSnapshot)
            .limit(pageSize)
            .get()
            .await()
            .toObjects(MessageDTO::class.java)

        return afterMessageList.sortedByDescending { it.sendAt }
    }

}