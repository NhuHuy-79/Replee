package com.nhuhuy.replee.core.database

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
import com.nhuhuy.replee.core.database.entity.file_path.FilePathDao
import com.nhuhuy.replee.core.database.entity.file_path.FilePathEntity
import com.nhuhuy.replee.core.database.entity.message.MessageDao
import com.nhuhuy.replee.core.database.entity.message.MessageEntity
import com.nhuhuy.replee.core.database.entity.message_action.MessageActionDao
import com.nhuhuy.replee.core.database.entity.message_action.MessageActionEntity
import com.nhuhuy.replee.core.database.entity.pager.MessageRemoteKey
import com.nhuhuy.replee.core.database.entity.pager.MessageRemoteKeyDao
import com.nhuhuy.replee.core.database.entity.search_history.SearchHistoryDao
import com.nhuhuy.replee.core.database.entity.search_history.SearchHistoryEntity

@Database(
    entities = [
        AccountEntity::class,
        ConversationEntity::class,
        MessageEntity::class,
        MessageRemoteKey::class,
        SearchHistoryEntity::class,
        FilePathEntity::class,
        MessageActionEntity::class],
    version = 42,
    exportSchema = true
)
@TypeConverters(CoreConverter::class)
abstract class CoreDatabase : RoomDatabase() {
    abstract fun provideAccountDao(): AccountDao
    abstract fun provideSearchHistoryDao(): SearchHistoryDao
    abstract fun provideConversationDao(): ConversationDao
    abstract fun provideMessageDao(): MessageDao
    abstract fun provideMessageRemoteKeyDao(): MessageRemoteKeyDao
    abstract fun provideFilePathDao(): FilePathDao
    abstract fun provideMessageActionDao(): MessageActionDao
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
