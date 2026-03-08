package com.nhuhuy.replee.core.database.entity.search_history

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nhuhuy.replee.core.database.entity.account.AccountEntity

@Entity(
    tableName = "search_history_table",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["uid"],
            childColumns = ["searchResultId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["uid"],
            childColumns = ["ownerId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
    ],
    indices = [
        Index("ownerId"),
        Index("searchResultId"),
        Index(value = ["ownerId", "searchResultId"], unique = true)
    ]
)
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val ownerId: String,
    val searchResultId: String,
    val searchAt: Long = System.currentTimeMillis()
)


