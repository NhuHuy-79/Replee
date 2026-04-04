package com.nhuhuy.replee.core.network.data_source

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
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
    private val database: DatabaseReference
) : PresenceNetworkDataSource {
    override suspend fun setOnline(userId: String) {

        val statusRef = database.child("status/$userId")

        val onlineStatus = mapOf(
            "state" to "online",
            "last_changed" to System.currentTimeMillis()
        )

        val offlineStatus = mapOf(
            "state" to "offline",
            "last_changed" to System.currentTimeMillis()
        )

        database.child(".info/connected")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val connected = snapshot.getValue(Boolean::class.java) ?: false
                    if (connected) {
                        statusRef.onDisconnect().setValue(offlineStatus)
                        statusRef.setValue(onlineStatus)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override suspend fun setOffline(userId: String) {
        database.child("status/$userId")
            .setValue(
                mapOf(
                    "state" to "offline",
                    "last_changed" to System.currentTimeMillis()
                )
            )
    }

    override fun observeUserStatus(userId: String): Flow<Boolean> = callbackFlow {
        val statusRef = database.child("status/$userId/state")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val state = snapshot.getValue(String::class.java)
                trySend(state == "online")
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        statusRef.addValueEventListener(listener)
        awaitClose { statusRef.removeEventListener(listener) }
    }
}