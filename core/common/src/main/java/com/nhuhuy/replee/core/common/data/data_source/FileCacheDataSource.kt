package com.nhuhuy.replee.core.common.data.data_source

import androidx.core.net.toUri
import javax.inject.Inject

interface FileCacheDataSource {
    suspend fun cacheImage(uriPath: String)
}

class FileCacheDataSourceImp @Inject constructor() : FileCacheDataSource {
    override suspend fun cacheImage(uriPath: String) {
        uriPath.toUri()
    }
}