package com.nhuhuy.replee.core.common.data

import androidx.core.net.toUri

class FileCacheDataSource {
    suspend fun cacheImage(uriPath: String) {
        uriPath.toUri()

    }
}