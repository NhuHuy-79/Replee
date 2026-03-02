package com.nhuhuy.replee.core.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nhuhuy.replee.core.database.converter.CoreConverter
import com.nhuhuy.replee.core.database.entity.account.AccountDao
import com.nhuhuy.replee.core.database.entity.account.AccountEntity
import com.nhuhuy.replee.core.database.entity.conversation.ConversationDao
import com.nhuhuy.replee.core.database.entity.conversation.ConversationEntity
import com.nhuhuy.replee.core.database.entity.message.MessageDao
import com.nhuhuy.replee.core.database.entity.message.MessageEntity
import com.nhuhuy.replee.core.database.entity.pager.MessageRemoteKey
import com.nhuhuy.replee.core.database.entity.pager.MessageRemoteKeyDao

@Database(
    entities = [AccountEntity::class, ConversationEntity::class, MessageEntity::class,
        MessageRemoteKey::class],
    version = 22,
    exportSchema = true,
    autoMigrations = [
        /* AutoMigration(
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
         ),
         AutoMigration(
             from = 13, to = 14
         ),
         AutoMigration(
             from = 14, to= 15
         ),*/
        AutoMigration(
            from = 19, to = 20
        ),
        AutoMigration(
            from = 20, to = 21
        )
    ]
)
@TypeConverters(CoreConverter::class)
abstract class CoreDatabase() : RoomDatabase(){
    abstract fun provideAccountDao() : AccountDao
    abstract fun provideConversationDao() : ConversationDao
    abstract fun provideMessageDao() : MessageDao
    abstract fun provideMessageRemoteKeyDao(): MessageRemoteKeyDao

}

val Migration_15_16 = object : Migration(15, 16) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS message_remote_keys")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS message_remote_keys (
                conversationId TEXT NOT NULL DEFAULT '',
                oldest_created_at INTEGER,
                end_reached INTEGER NOT NULL DEFAULT 0,
                PRIMARY KEY(conversationId)
            )
        """.trimIndent()
        )
    }
}