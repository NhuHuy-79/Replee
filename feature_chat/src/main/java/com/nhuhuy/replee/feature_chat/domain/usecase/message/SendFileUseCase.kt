package com.nhuhuy.replee.feature_chat.domain.usecase.message

import com.nhuhuy.core.domain.repository.FileRepository
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.domain.model.MessageStatus
import com.nhuhuy.replee.feature_chat.domain.model.MessageType
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import java.util.UUID
import javax.inject.Inject

class SendFileUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
    private val fileRepository: FileRepository
) {
    suspend operator fun invoke(
        senderId: String,
        receiverId: String,
        uriPath: String,
        conversationId: String
    ) {
        val raw = Message(
            messageId = UUID.randomUUID().toString(),
            conversationId = conversationId,
            senderId = senderId,
            receiverId = receiverId,
            status = MessageStatus.PENDING,
            content = "",
            sentAt = System.currentTimeMillis(),
            seen = false,
            localUriPath = uriPath,
            type = MessageType.IMAGE
        )

        val messageId: String = messageRepository.saveMessage(raw)

        //Scheduler
        fileRepository.scheduleUploadFile(
            messageId = messageId,
            uriPath = uriPath
        )
    }
}