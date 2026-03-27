package com.nhuhuy.replee.feature_chat.domain.usecase.file

import com.nhuhuy.core.domain.model.FilePath
import com.nhuhuy.core.domain.repository.FileMetadata
import com.nhuhuy.core.domain.repository.FileRepository
import com.nhuhuy.replee.feature_chat.domain.model.message.Message
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageStatus
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageType
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class SendFileMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
    private val fileRepository: FileRepository
) {
    suspend operator fun invoke(
        senderId: String,
        receiverId: String,
        uriPath: String,
        conversationId: String
    ) {
        val metadataFile: FileMetadata = fileRepository.getFileMetadata(uriPath)
        val newUri: String = fileRepository.saveFileToInternalStorage(uriPath)
        val raw = Message(
            messageId = UUID.randomUUID().toString(),
            conversationId = conversationId,
            senderId = senderId,
            receiverId = receiverId,
            status = MessageStatus.PENDING,
            content = "",
            sentAt = System.currentTimeMillis(),
            seen = false,
            localUriPath = newUri,
            type = MessageType.IMAGE
        )

        val messageId: String = messageRepository.saveMessage(raw)

        //saveToInternalStorage and return a local path.
        val filePath = FilePath(
            messageId = raw.messageId,
            localPath = newUri,
            width = metadataFile.width,
            height = metadataFile.height,
            fileType = metadataFile.mimeType,
            fileSize = metadataFile.size,
        )

        Timber.d("Filepath: $filePath")

        //create LocalFilePath with metadata and local path.
        fileRepository.upsertFilePath(filePath)

        //Scheduler will only get MessageID
        fileRepository.scheduleUploadFile(
            messageId = messageId,
            uriPath = uriPath
        )
    }
}