package com.nhuhuy.replee.core.network.api

import com.nhuhuy.replee.core.network.api.model.ConversationNotificationRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

object Route{
    const val HEADER = "device-token"
    const val URL = "http://192.168.1.5:3000/api/v1/notifications"
    const val POST = "$URL/send"
}



interface KtorService{
    suspend fun sendConversationMessage(
        authenticationId: String,
        deviceToken: String,
        request: ConversationNotificationRequest
    )
}

class KtorServiceImp @Inject constructor(
    private val client: HttpClient
): KtorService{
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