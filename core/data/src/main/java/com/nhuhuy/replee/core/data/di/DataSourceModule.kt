package com.nhuhuy.replee.core.data.di

import com.nhuhuy.replee.core.data.data_source.file_path.FilePathLocalDataSource
import com.nhuhuy.replee.core.data.data_source.file_path.FilePathLocalDataSourceImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun bindFilePathLocalDataSource(
        imp: FilePathLocalDataSourceImp
    ): FilePathLocalDataSource
}
