package com.nhuhuy.replee.core.database.entity.account

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nhuhuy.replee.core.database.entity.account.AccountEntity

@Database(
    entities = [AccountEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AccountDatabase() : RoomDatabase(){
    abstract fun provideDao() : AccountDao
}
