package com.nhuhuy.replee.core.network.api

import com.nhuhuy.replee.core.network.api.model.CloudinaryResponse
import com.nhuhuy.replee.core.network.api.model.ConversationNotificationRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.utils.io.core.Input
import javax.inject.Inject

object Route{
    const val HEADER = "device-token"
    const val URL = "https://replee-deploy.vercel.app/api/v1/notifications"
    const val POST = "$URL/send"

    const val CLOUDINARY_URL = "https"
}



interface KtorService{
    suspend fun uploadFile(
        cloudName: String,
        uploadPreset: String,
        fileInput: Input,
        fileName: String,
        mimeType: String,
    ): CloudinaryResponse
    suspend fun sendConversationMessage(
        authenticationId: String,
        deviceToken: String,
        request: ConversationNotificationRequest
    )
}

class KtorServiceImp @Inject constructor(
    private val client: HttpClient
): KtorService{
    override suspend fun uploadFile(
        cloudName: String,
        uploadPreset: String,
        fileInput: Input,
        fileName: String,
        mimeType: String
    ): CloudinaryResponse {
        val url = "https://api.cloudinary.com/v1_1/dgq6g8u5h/image/upload"

        val response = client.submitFormWithBinaryData(
            url = url,
            formData = formData {
                append("upload_preset", uploadPreset.trim())
                appendInput(
                    key = "file",
                    headers = Headers.build {
                        append(HttpHeaders.ContentType, mimeType)
                        append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                    },
                    block = { fileInput }
                )
            }
        )

        if (response.status.value in 200..299) {
            return response.body()
        } else {
            val errorText = response.body<String>()
            throw Exception("Cloudinary Upload Failed: $errorText")
        }
    }

    override suspend fun sendConversationMessage(
        authenticationId: String,
        deviceToken: String,
        request: ConversationNotificationRequest
    ) {
        client.post(Route.POST) {
            bearerAuth(authenticationId)
            header(Route.HEADER, deviceToken)
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

}