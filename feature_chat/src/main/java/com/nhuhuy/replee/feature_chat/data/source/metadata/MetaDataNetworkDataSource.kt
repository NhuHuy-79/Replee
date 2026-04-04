package com.nhuhuy.replee.feature_chat.data.source.metadata

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface MetaDataNetworkDataSource {
    //Metadata typing
    fun observeTyping(conversationId: String): Flow<Map<String, Boolean>>
    suspend fun setTyping(
        conversationId: String,
        userId: String,
        typing: Boolean
    )

    //Metadata last reading time
    //fun observeLastReadingTime(conversationId: String) : Flow<Map<String, Long>>
    //suspend fun setLastReadingTime(conversationId: String, userId: String, reading: Long)
}

class MetaDataNetworkDataSourceImpl @Inject constructor(
    private val database: DatabaseReference
) : MetaDataNetworkDataSource {
    override fun observeTyping(
        conversationId: String,
    ): Flow<Map<String, Boolean>> = callbackFlow {
        val ref = database.child("metadata/typing").child(conversationId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.value as? Map<String, Boolean> ?: emptyMap()
                trySend(data)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override suspend fun setTyping(
        conversationId: String,
        userId: String,
        typing: Boolean
    ) {
        val ref = database.child("metadata/typing")
            .child(conversationId)
            .child(userId)

        if (typing) {
            ref.setValue(true).await()
            ref.onDisconnect().removeValue().await()
        } else {
            ref.removeValue().await()
        }
    }

}