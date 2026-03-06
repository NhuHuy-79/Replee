package com.nhuhuy.replee.core.network.data_source


import android.annotation.SuppressLint
import androidx.core.net.toUri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.nhuhuy.core.domain.model.FileState
import com.nhuhuy.core.domain.model.FileUploadException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface UploadFileService {
    suspend fun uploadFiles(uriPaths: List<String>): List<String>

    //Return a list contains message ids.
    suspend fun uploadMessageWithUri(messageAndUri: Map<String, String>): Map<String, String>
    suspend fun uploadImageWithByteArray(byteArray: ByteArray): String
    suspend fun uploadImageWithUriPath(uriPath: String): String
    fun observeUploadFile(uriPath: String): Flow<FileState>
}

class CloudinaryFileUploader @Inject constructor(
    private val mediaManager: MediaManager,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UploadFileService {
    @SuppressLint("Recycle")
    override suspend fun uploadImageWithUriPath(uriPath: String): String {
        return withContext(ioDispatcher) {
            val uri = uriPath.toUri()
            suspendCancellableCoroutine { continuation ->
                val requestId = mediaManager
                    .upload(uri)
                    .unsigned("replee_upload_avatar")
                    .option("resource_type", "image")
                    .option("folder", "replee/chat")
                    /*.option("transformation", "c_limit,w_1280,q_auto,f_auto")*/
                    //Use transform for backend!
                    .option("quality", "auto")
                    .option("fetch_format", "auto")
                    .callback(object : UploadCallback {

                        override fun onSuccess(
                            requestId: String?,
                            resultData: Map<*, *>
                        ) {
                            val url = resultData["secure_url"] as String
                            if (continuation.isActive) {
                                continuation.resume(url)
                            }
                        }

                        override fun onError(
                            requestId: String?,
                            error: ErrorInfo?
                        ) {
                            if (continuation.isActive) {
                                continuation.resumeWithException(
                                    Exception(error?.description ?: "Upload error")
                                )
                            }
                        }

                        override fun onStart(requestId: String?) {}

                        override fun onProgress(
                            requestId: String?,
                            bytes: Long,
                            totalBytes: Long
                        ) {
                        }

                        override fun onReschedule(
                            requestId: String?,
                            error: ErrorInfo?
                        ) {
                        }
                    })
                    .dispatch()

                // 👇 Quan trọng: cancel upload nếu coroutine bị huỷ
                continuation.invokeOnCancellation {
                    MediaManager.get().cancelRequest(requestId)
                }
            }
        }

    }

    override fun observeUploadFile(uriPath: String): Flow<FileState> {
        val uri = uriPath.toUri()
        return callbackFlow {
            val requestId = mediaManager
                .upload(uri)
                .unsigned("replee_upload_avatar")
                .option("resource_type", "image")
                .option("folder", "replee/avatars")
                .option("quality", "auto")
                .option("fetch_format", "auto")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {
                        trySend(FileState.Loading)
                    }

                    override fun onProgress(
                        requestId: String?,
                        bytes: Long,
                        totalBytes: Long
                    ) {
                        val progress = if (totalBytes > 0)
                            bytes.toFloat() / totalBytes.toFloat()
                        else 0f
                        trySend(FileState.Progress(progress))
                    }

                    override fun onSuccess(
                        requestId: String?,
                        resultData: Map<*, *>?
                    ) {
                        val secureUrl = resultData?.get("secure_url")?.toString()

                        if (secureUrl == null) {
                            val exception =
                                FileUploadException("Cloudinary upload success but secure_url is null")
                            trySend(FileState.Failure(exception))
                        } else {
                            trySend(FileState.Success(secureUrl))
                        }
                        close()
                    }

                    override fun onError(
                        requestId: String?,
                        error: ErrorInfo?
                    ) {
                        val exception = FileUploadException(error?.description ?: "Unknow")
                        trySend(FileState.Failure(exception))
                        close(exception)
                    }

                    override fun onReschedule(
                        requestId: String?,
                        error: ErrorInfo?
                    ) {
                        //Do nothing
                    }

                }
                ).dispatch()

            awaitClose {
                mediaManager.cancelRequest(requestId)
            }
        }
    }

    suspend fun uploadImage(bytes: ByteArray): String {
        return suspendCancellableCoroutine { cont ->
            val requestId = MediaManager.get().upload(bytes)
                .unsigned("replee_upload_avatar")
                .option("resource_type", "image")
                .option("folder", "replee/avatars")
                .option("quality", "auto")
                .option("fetch_format", "auto")
                .callback(object : UploadCallback {

                    override fun onStart(requestId: String?) = Unit

                    override fun onProgress(
                        requestId: String?,
                        bytes: Long,
                        totalBytes: Long
                    ) = Unit

                    override fun onSuccess(
                        requestId: String?,
                        resultData: Map<*, *>?
                    ) {
                        val secureUrl = resultData?.get("secure_url") as? String

                        if (secureUrl.isNullOrBlank()) {
                            cont.resumeWithException(
                                IllegalStateException("Cloudinary upload success but secure_url is null")
                            )
                            return
                        }

                        cont.resume(secureUrl)
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        cont.resumeWithException(
                            RuntimeException(error?.description ?: "Upload failed")
                        )
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                        cont.resumeWithException(
                            RuntimeException(error?.description ?: "Upload rescheduled")
                        )
                    }
                })
                .dispatch()

            cont.invokeOnCancellation {
                try {
                    MediaManager.get().cancelRequest(requestId)
                } catch (_: Exception) {
                }
            }
        }
    }

    override suspend fun uploadFiles(uriPaths: List<String>): List<String> {
        return withContext(ioDispatcher) {
            coroutineScope {
                uriPaths.map { uri ->
                    async {
                        uploadImageWithUriPath(uri)
                    }
                }.awaitAll()
            }
        }
    }

    override suspend fun uploadMessageWithUri(
        messageAndUri: Map<String, String>
    ): Map<String, String> {

        return withContext(ioDispatcher) {
            coroutineScope {
                messageAndUri.map { (messageId, uri) ->
                    async {
                        val remoteUrl = uploadImageWithUriPath(uriPath = uri)
                        messageId to remoteUrl
                    }
                }.awaitAll()
            }.toMap()
        }
    }

    override suspend fun uploadImageWithByteArray(byteArray: ByteArray): String {
        return uploadImage(byteArray)
    }
}