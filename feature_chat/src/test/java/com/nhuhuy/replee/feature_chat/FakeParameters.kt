package com.nhuhuy.replee.feature_chat

import com.nhuhuy.replee.feature_chat.domain.model.message.Message
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageStatus

class FakeParameters {
    companion object {
        val fakeMessage = Message(
            messageId = "123",
            conversationId = "123",
            senderId = "123",
            receiverId = "123",
            content = "Hello",
            seen = false,
            sentAt = System.currentTimeMillis(),
            status = MessageStatus.PENDING,
        )

        val fakeException = Exception("Fake")
    }
}