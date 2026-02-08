package com.nhuhuy.replee.core.firebase.data_source

import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CloudifyFileUploadService {

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
}