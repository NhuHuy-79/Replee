package com.nhuhuy.replee.core.common.data

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class UriToFileConverter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun toByteArray(uri: Uri): ByteArray? = withContext(ioDispatcher) {
        val resolver = context.contentResolver
        val bytes = resolver.openInputStream(uri)
            ?.use { it.readBytes() }
        return@withContext bytes
    }

    suspend fun toTempFileFrom(uri: Uri): File? = withContext(ioDispatcher) {
        val resolver = context.contentResolver

        val tempFile = File(
            context.cacheDir,
            "upload_${System.currentTimeMillis()}.jpg"
        )

        resolver.openInputStream(uri)?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        } ?: return@withContext null

        tempFile
    }
}