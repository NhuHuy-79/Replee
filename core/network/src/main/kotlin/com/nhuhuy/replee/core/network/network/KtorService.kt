package com.nhuhuy.replee.core.network.network

import com.nhuhuy.replee.core.network.network.model.ConversationNotificationRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

object Route{
    const val HEADER = "Device-Token"
    const val URL = "http://10.0.2.2:8080"
    const val POST = "$URL/post/conversation"
}



interface KtorService{
    suspend fun sendConversationMessage(token: String, request: ConversationNotificationRequest)
}

class KtorServiceImp @Inject constructor(
    private val client: HttpClient
): KtorService{
    override suspend fun sendConversationMessage(
        token: String,
        request: ConversationNotificationRequest
    ) {
        client.post(Route.POST) {
            bearerAuth(request.senderId)
            header(Route.HEADER, token)
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

}