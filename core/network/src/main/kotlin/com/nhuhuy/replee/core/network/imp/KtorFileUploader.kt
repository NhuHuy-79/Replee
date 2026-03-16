package com.nhuhuy.replee.core.network.imp

import android.content.Context
import android.webkit.MimeTypeMap
import com.nhuhuy.core.domain.model.UploadFileState
import com.nhuhuy.replee.core.network.api.KtorService
import com.nhuhuy.replee.core.network.data_source.UploadFileService
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.utils.io.streams.asInput
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.IOException
import javax.inject.Inject

class KtorFileUploader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ktorService: KtorService
) : UploadFileService {
    override suspend fun uploadFiles(uriPaths: List<String>): List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadMessageWithUri(messageAndUri: Map<String, String>): Map<String, String> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadImageWithByteArray(byteArray: ByteArray): String {
        TODO("Not yet implemented")
    }

    override suspend fun uploadImageWithUriPath(uriPath: String): String {
        //Path of tempFile
        val file = File(uriPath)

        if (!file.exists()) throw IOException("File tạm không tồn tại: $uriPath")

        return try {
            val response = ktorService.uploadFile(
                cloudName = "dgq6g8u5h",
                uploadPreset = "replee_chat_img",
                fileInput = file.inputStream().asInput(),
                fileName = file.name,
                mimeType = "image/jpeg"
            )
            response.secureUrl
        } catch (e: Exception) {
            throw e
        }
    }

    override fun observeUploadFile(uriPath: String): Flow<UploadFileState> {
        TODO("Not yet implemented")
    }

    private fun getMimeType(fileName: String): String {
        val extension = fileName.substringAfterLast(".", "")
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            ?: "application/octet-stream"
    }

}