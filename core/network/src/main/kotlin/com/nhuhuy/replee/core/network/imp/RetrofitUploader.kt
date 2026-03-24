package com.nhuhuy.replee.core.network.imp

import android.webkit.MimeTypeMap
import com.nhuhuy.core.domain.model.UploadFileState
import com.nhuhuy.replee.core.network.api.cloudinary.CloudinaryApi
import com.nhuhuy.replee.core.network.data_source.UploadFileService
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import javax.inject.Inject

class RetrofitUploader @Inject constructor(
    private val api: CloudinaryApi
) : UploadFileService {

    override suspend fun uploadImageWithOption(
        uriPath: String,
        folder: String,
        option: Map<String, String>
    ): String {
        val file = File(uriPath)
        if (!file.exists()) throw IOException("Fil doesn't exist: $uriPath")

        val extension = MimeTypeMap.getFileExtensionFromUrl(uriPath)
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "image/*"

        val requestFile = file.readBytes().toRequestBody(
            mimeType.toMediaTypeOrNull(),
            0, file.length().toInt()
        )

        val filePart = MultipartBody.Part.createFormData(
            "file",
            file.name,
            requestFile
        )

        val presetPart = "replee_chat_img".toRequestBody("text/plain".toMediaTypeOrNull())
        val folderPart = folder.toRequestBody("text/plain".toMediaTypeOrNull())

        val qualityPart = "auto".toRequestBody("text/plain".toMediaTypeOrNull())

        return try {
            val response = api.uploadImage(
                cloudName = "dgq6g8u5h",
                file = filePart,
                preset = presetPart,

                folder = folderPart,
                quality = qualityPart
            )

            response.body()?.secureUrl ?: throw IOException(
                "Upload failed: ${
                    response.errorBody()?.string()
                }"
            )
        } catch (e: Exception) {
            throw IOException("Error: ${e.message}")
        }
    }

    override suspend fun uploadMessageWithUri(messageAndUri: Map<String, String>): Map<String, String> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadImageWithUriPath(uriPath: String): String {
        val file = File(uriPath)

        if (!file.exists()) throw IOException("File doesn't exist : $uriPath")

        val content = file.readBytes()
        val requestFile = content.toRequestBody(
            "image/jpeg".toMediaTypeOrNull(),
            0, content.size
        )

        val filePart = MultipartBody.Part.createFormData(
            "file",
            "${file.name}.jpg",
            requestFile
        )

        val presetPart = "replee_chat_img"
            .toRequestBody("text/plain".toMediaTypeOrNull())

        return api.uploadImage(filePart, presetPart).body()?.secureUrl
            ?: throw IOException("Upload failed")
    }

    override fun observeUploadFile(uriPath: String): Flow<UploadFileState> {
        TODO("Not yet implemented")
    }

}