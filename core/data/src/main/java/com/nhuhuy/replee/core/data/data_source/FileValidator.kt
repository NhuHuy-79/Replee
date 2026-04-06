package com.nhuhuy.replee.core.data.data_source

import android.content.Context
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import com.nhuhuy.core.domain.model.ValidateFileResult
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class FileValidator @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        const val MAX_FILE_SIZE = 25 * 1024 * 1024
        val ALLOWED_EXTENSIONS = listOf(
            //Image
            "jpg", "jpeg", "png", "webp", "heic", "heif", "gif",
            // Video
            "mp4", "mkv", "mov", "3gp", "avi",
            // document
            "pdf", "doc", "docx", "txt"
        )
    }

    fun validate(uriPath: String): ValidateFileResult {
        return try {
            val fileUri = uriPath.toUri()
            val contentResolver = context.contentResolver

            var extension = uriPath.substringAfterLast(".", "").lowercase()


            Timber.d("DEBUG_REPLEE: Path là $uriPath")
            Timber.d("DEBUG_REPLEE: Đuôi file cắt được là '$extension'")

            if (extension.isEmpty()) {
                val mimeType = contentResolver.getType(fileUri) ?: "application/octet-stream"
                extension =
                    MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)?.lowercase() ?: ""
            }

            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                return ValidateFileResult.UnSupported
            }


            var fileSize = 0L
            if (fileUri.scheme == "content") {
                contentResolver.query(fileUri, arrayOf(OpenableColumns.SIZE), null, null, null)
                    ?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                            if (sizeIndex != -1) fileSize = cursor.getLong(sizeIndex)
                        }
                    }
            }

            if (fileSize <= 0) {
                val path = fileUri.path // Lấy đường dẫn /data/user/0/...
                if (path != null) {
                    val file = File(path)
                    if (file.exists()) {
                        fileSize = file.length()
                    }
                }
            }

            if (fileSize > MAX_FILE_SIZE) {
                val sizeInMB = fileSize / (1024 * 1024)
                Timber.d("FileSize: $sizeInMB MB")
                return ValidateFileResult.FileTooLarge
            }

            if (fileSize <= 0) {
                return ValidateFileResult.Unknown
            }

            ValidateFileResult.Valid
        } catch (e: Exception) {
            Timber.e(e)
            ValidateFileResult.Unknown
        }

    }
}