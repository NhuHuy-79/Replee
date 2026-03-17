package com.nhuhuy.replee.core.network.imp

import com.nhuhuy.core.domain.model.UploadFileState
import com.nhuhuy.replee.core.network.api.cloudinary.CloudinaryApi
import com.nhuhuy.replee.core.network.data_source.UploadFileService
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import javax.inject.Inject

class RetrofitUploader @Inject constructor(
    private val api: CloudinaryApi
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
        val file = File(uriPath)

        if (!file.exists()) throw IOException("File tạm không tồn tại: $uriPath")

        val requestFile = RequestBody.create(
            "image/jpeg".toMediaTypeOrNull(),
            file.readBytes()
        )

        val filePart = MultipartBody.Part.createFormData(
            "file",
            "${file.name}.jpg",
            requestFile
        )

        val presetPart = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            "replee_chat_img"
        )

        return api.uploadImage(filePart, presetPart).body()?.secureUrl
            ?: throw IOException("Upload failed")
    }

    override fun observeUploadFile(uriPath: String): Flow<UploadFileState> {
        TODO("Not yet implemented")
    }

}