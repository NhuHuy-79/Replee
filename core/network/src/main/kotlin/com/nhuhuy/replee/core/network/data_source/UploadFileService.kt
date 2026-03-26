package com.nhuhuy.replee.core.network.data_source


import com.nhuhuy.core.domain.model.UploadFileState
import kotlinx.coroutines.flow.Flow

interface UploadFileService {
    suspend fun uploadImageWithOption(
        uriPath: String,
        folder: String,
        option: Map<String, String>
    ): String
    //Return a list contains message ids.
    suspend fun uploadMessageWithUri(messageAndUri: Map<String, String>): Map<String, String>
    suspend fun uploadImageWithUriPath(uriPath: String): String
    fun observeUploadFile(uriPath: String): Flow<UploadFileState>
}


