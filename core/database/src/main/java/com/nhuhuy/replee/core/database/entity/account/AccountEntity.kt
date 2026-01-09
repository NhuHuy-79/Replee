package com.nhuhuy.replee.core.database.entity.account

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = false)
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val createAt: Long? = null,
    val logOut: Boolean = false,
)