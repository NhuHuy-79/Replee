package com.nhuhuy.replee.core.database.entity.account

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = false)
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val createAt: Long? = null,
    @ColumnInfo(name = "imageUrl", defaultValue = "")
    val imageUrl: String = "",
    @ColumnInfo(name = "blockedUserList", defaultValue = "[]")
    val blockedUserList: List<String> = emptyList(),
    @ColumnInfo(name = "provider", defaultValue = "EMAIL")
    val provider: String = "EMAIL",
    val logOut: Boolean = false,
    @ColumnInfo(name = "isOnline", defaultValue = "0")
    val isOnline: Boolean = false,
    @ColumnInfo(name = "lastSeen", defaultValue = "0")
    val lastSeen: Long = 0L
)