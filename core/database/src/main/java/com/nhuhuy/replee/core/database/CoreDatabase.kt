package com.nhuhuy.replee.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nhuhuy.replee.core.database.entity.account.AccountDao
import com.nhuhuy.replee.core.database.entity.account.AccountEntity
import com.nhuhuy.replee.core.database.entity.conversation.ConversationDao
import com.nhuhuy.replee.core.database.entity.conversation.ConversationEntity
import com.nhuhuy.replee.core.database.entity.message.MessageDao
import com.nhuhuy.replee.core.database.entity.message.MessageEntity

@Database(
    entities = [AccountEntity::class, ConversationEntity::class, MessageEntity::class],
    version = 3,
    exportSchema = false,
)
abstract class CoreDatabase() : RoomDatabase(){
    abstract fun provideAccountDao() : AccountDao
    abstract fun provideConversationDao() : ConversationDao
    abstract fun provideMessageDao() : MessageDao
}