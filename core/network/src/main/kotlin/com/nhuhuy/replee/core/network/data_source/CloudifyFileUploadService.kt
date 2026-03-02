package com.nhuhuy.replee.core.network.data_source


import android.annotation.SuppressLint
import androidx.core.net.toUri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface FileLocalDataSource {
    suspend fun uploadImageWithByteArray(byteArray: ByteArray): String
    suspend fun uploadImageWithUriPath(uriPath: String): String
}

class CloudifyFileUploadService @Inject constructor() : FileLocalDataSource {
    @SuppressLint("Recycle")
    override suspend fun uploadImageWithUriPath(uriPath: String): String {
        return withContext(Dispatchers.IO) {
            val uri = uriPath.toUri()
            suspendCancellableCoroutine { continuation ->
                val requestId = MediaManager.get()
                    .upload(uri)
                    .unsigned("replee_upload_avatar")
                    .option("resource_type", "image")
                    .option("folder", "replee/chat")
                    .option("transformation", "c_limit,w_1280,q_auto,f_auto")
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

    override suspend fun uploadImageWithByteArray(byteArray: ByteArray): String {
        return uploadImage(byteArray)
    }
}