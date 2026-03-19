package com.nhuhuy.replee.core.common.data.data_source

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import com.nhuhuy.core.domain.repository.FileMetadata
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class FileStorageDataSource @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    @ApplicationContext private val context: Context
) {
    suspend fun saveToInternalStorage(uri: Uri): String {
        return withContext(ioDispatcher) {
            val fileName = "replee_${System.currentTimeMillis()}"
            val file = File(context.filesDir, fileName)

            context.contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        }
    }

    @SuppressLint("ExifInterface")
    suspend fun getFileMetadata(uriPath: String): FileMetadata {
        val fileUri = uriPath.toUri()
        var width = 0
        var height = 0
        var size = 0L
        var mimeType = ""

        // 1. Lấy thông tin cơ bản từ ContentResolver (Size & MimeType)
        context.contentResolver.query(fileUri, null, null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.getColumnIndex(DocumentsContract.Document.COLUMN_MIME_TYPE)
            if (cursor.moveToFirst()) {
                size = cursor.getLong(sizeIndex)
            }
        }
        mimeType = context.contentResolver.getType(fileUri) ?: "application/octet-stream"

        try {
            if (mimeType.startsWith("image")) {
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                context.contentResolver.openInputStream(fileUri).use {
                    BitmapFactory.decodeStream(it, null, options)
                }

                var rawWidth = options.outWidth
                var rawHeight = options.outHeight

                // 2. Xử lý EXIF Orientation (Cái này quan trọng này!)
                context.contentResolver.openInputStream(fileUri).use { input ->
                    if (input != null) {
                        val exif = ExifInterface(input)
                        val orientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL
                        )

                        // Nếu ảnh bị xoay 90 hoặc 270 độ, ta đảo ngược Width/Height
                        if (orientation == ExifInterface.ORIENTATION_ROTATE_90 ||
                            orientation == ExifInterface.ORIENTATION_ROTATE_270
                        ) {
                            val temp = rawWidth
                            rawWidth = rawHeight
                            rawHeight = temp
                        }
                    }
                }
                width = rawWidth
                height = rawHeight

            } else if (mimeType.startsWith("image")) {
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                context.contentResolver.openInputStream(fileUri).use {
                    BitmapFactory.decodeStream(it, null, options)
                }
                width = options.outWidth
                height = options.outHeight


            } else if (mimeType.startsWith("video")) {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(context, fileUri)
                val videoWidth =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                        ?.toInt() ?: 0
                val videoHeight =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                        ?.toInt() ?: 0
                val rotation =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
                        ?.toInt() ?: 0
                retriever.release()

                if (rotation == 90 || rotation == 270) {
                    width = videoHeight
                    height = videoWidth
                } else {
                    width = videoWidth
                    height = videoHeight
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
        }

        return FileMetadata(
            width = width,
            height = height,
            size = size,
            mimeType = mimeType,
            extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: ""
        )
    }
}