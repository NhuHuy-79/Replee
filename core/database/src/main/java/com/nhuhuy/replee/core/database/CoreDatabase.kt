package com.nhuhuy.replee.core.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nhuhuy.replee.core.database.converter.CoreConverter
import com.nhuhuy.replee.core.database.entity.account.AccountDao
import com.nhuhuy.replee.core.database.entity.account.AccountEntity
import com.nhuhuy.replee.core.database.entity.conversation.ConversationDao
import com.nhuhuy.replee.core.database.entity.conversation.ConversationEntity
import com.nhuhuy.replee.core.database.entity.message.MessageDao
import com.nhuhuy.replee.core.database.entity.message.MessageEntity

@Database(
    entities = [AccountEntity::class, ConversationEntity::class, MessageEntity::class],
    version = 13,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(
            from = 4, to = 5
        ),
        AutoMigration(
            from = 5, to = 6
        ),
        AutoMigration(
            from = 6, to = 7
        ),
        AutoMigration(
            from = 7, to = 8
        ),
        AutoMigration(
            from = 8, to = 9
        ),
        AutoMigration(
            from = 9, to = 10
        ),
        AutoMigration(
            from = 10, to = 11
        ),
        AutoMigration(
            from = 11, to = 12
        ),
        AutoMigration(
            from = 12, to = 13
        )
    ]
)
@TypeConverters(CoreConverter::class)
abstract class CoreDatabase() : RoomDatabase(){
    abstract fun provideAccountDao() : AccountDao
    abstract fun provideConversationDao() : ConversationDao
    abstract fun provideMessageDao() : MessageDao
}