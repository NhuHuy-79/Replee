package com.nhuhuy.replee.feature_chat.data.source.presence

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

interface PresenceNetworkDataSource {
    suspend fun setOnline(userId: String)

    suspend fun setOffline(userId: String)

    fun observeUserStatus(userId: String): Flow<Boolean>
}

class FirebasePresenceDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val realtimeDb: FirebaseDatabase
) : PresenceNetworkDataSource {
    override suspend fun setOnline(userId: String) {

        val statusRef = realtimeDb.getReference("status/$userId")

        val onlineStatus = mapOf(
            "state" to "online",
            "last_changed" to System.currentTimeMillis()
        )

        val offlineStatus = mapOf(
            "state" to "offline",
            "last_changed" to System.currentTimeMillis()
        )

        realtimeDb.getReference(".info/connected")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.getValue(Boolean::class.java) == false) return

                    statusRef.onDisconnect().setValue(offlineStatus)

                    statusRef.setValue(onlineStatus)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override suspend fun setOffline(userId: String) {

        realtimeDb.getReference("status/$userId")
            .setValue(
                mapOf(
                    "state" to "offline",
                    "last_changed" to System.currentTimeMillis()
                )
            )
    }

    override fun observeUserStatus(userId: String): Flow<Boolean> = callbackFlow {

        val listener = firestore
            .collection("users")
            .document(userId)
            .addSnapshotListener { snapshot, _ ->

                val online = snapshot?.getBoolean("online") ?: false

                trySend(online)
            }

        awaitClose { listener.remove() }
    }
}