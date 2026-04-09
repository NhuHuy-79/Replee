package com.nhuhuy.replee.feature_chat.usecase.message

import com.nhuhuy.core.domain.repository.FileMetadata
import com.nhuhuy.core.domain.repository.FileRepository
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageType
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import com.nhuhuy.replee.feature_chat.domain.usecase.file.SendFileMessageUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SendFileUseCaseTest {
    private lateinit var messageRepository: MessageRepository
    private lateinit var fileRepository: FileRepository

    private lateinit var useCase: SendFileMessageUseCase

    private val fakeMetadata = FileMetadata(
        width = 100,
        height = 100,
        mimeType = "image/jpeg",
    )

    @Before
    fun setUp() {
        messageRepository = mockk(relaxed = true)
        fileRepository = mockk(relaxed = true)
        useCase = SendFileMessageUseCase(
            messageRepository = messageRepository,
            fileRepository = fileRepository
        )
    }

    @Test
    fun `should save message and schedule upload when invoked`() = runTest {
        val senderId = "user_1"
        val receiverId = "user_2"
        val uriPath = "content://media/external/images/1"
        val conversationId = "conv_123"
        val internalUri = "internal/path/image.jpg"

        val mockMetadata = FileMetadata(
            width = 1080, height = 1920,
            mimeType = "image/jpeg", size = 1024L
        )

        coEvery { fileRepository.getFileMetadata(uriPath) } returns mockMetadata
        coEvery { fileRepository.saveFileToInternalStorage(uriPath) } returns internalUri
        coEvery { messageRepository.saveMessage(any()) } returns "random_id_from_repo"

        useCase(repliedMessage = null, senderId, receiverId, uriPath, conversationId)

        coVerify {
            messageRepository.saveMessage(match {
                it.senderId == senderId &&
                        it.receiverId == receiverId &&
                        it.localUriPath == internalUri &&
                        it.type == MessageType.IMAGE
            })
        }

        coVerify {
            fileRepository.upsertFilePath(match {
                it.localPath == internalUri &&
                        it.width == mockMetadata.width &&
                        it.fileSize == mockMetadata.size
            })
        }

        coVerify {
            fileRepository.scheduleUploadFile(
                messageId = any(),
                uriPath = uriPath
            )
        }
    }

}