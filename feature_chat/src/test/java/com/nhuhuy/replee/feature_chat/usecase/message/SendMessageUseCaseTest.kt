package com.nhuhuy.replee.feature_chat.usecase.message

import com.google.common.truth.Truth
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.feature_chat.FakeParameters.Companion.fakeException
import com.nhuhuy.replee.feature_chat.FakeParameters.Companion.fakeMessage
import com.nhuhuy.replee.feature_chat.data.SyncManager
import com.nhuhuy.replee.feature_chat.data.worker.WorkerScheduler
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import com.nhuhuy.replee.feature_chat.domain.repository.PushNotificationRepository
import com.nhuhuy.replee.feature_chat.domain.usecase.message.SendMessageUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SendMessageUseCaseTest {
    private lateinit var messageRepository: MessageRepository
    private lateinit var conversationRepository: ConversationRepository
    private lateinit var pushNotificationRepository: PushNotificationRepository
    private lateinit var syncManager: SyncManager
    private lateinit var workerScheduler: WorkerScheduler

    private lateinit var useCase: SendMessageUseCase

    @Before
    fun setUp() {
        messageRepository = mockk(relaxed = true)
        conversationRepository = mockk(relaxed = true)
        pushNotificationRepository = mockk(relaxed = true)
        workerScheduler = mockk(relaxed = true)
        syncManager = mockk(relaxed = true)

        useCase = SendMessageUseCase(
            messageRepository = messageRepository,
            conversationRepository = conversationRepository,
            pushNotificationRepository = pushNotificationRepository,
            syncManager = syncManager,
            workerScheduler = workerScheduler
        )
    }

    @Test
    fun `Should return failure when send message fails`() = runTest {
        //Arrange
        coEvery {
            messageRepository.sendMessage(any())
        } returns NetworkResult.Failure(fakeException)

        coEvery {
            conversationRepository.updateMetadataConversation(any())
        } returns NetworkResult.Success(Unit)

        coEvery {
            syncManager.updateConversationStatus(fakeMessage.conversationId, true)
        } returns Unit

        coEvery {
            syncManager.updateMessageStatus(any(), status = any())
        } returns Unit

        coEvery {
            pushNotificationRepository.pushNotification(any())
        } returns NetworkResult.Success(Unit)

        //Act
        val useCase = useCase(
            senderId = fakeMessage.senderId,
            receiverId = fakeMessage.receiverId,
            conversationId = fakeMessage.conversationId,
            text = fakeMessage.content,
        )

        val expected = NetworkResult.Failure(fakeException)

        //Assert
        Truth.assertThat(useCase).isEqualTo(expected)

        coVerify {
            messageRepository.sendMessage(any())
            syncManager.updateMessageStatus(any(), status = any())
            workerScheduler.scheduleMessageSyncWorker()
        }

    }

    @Test
    fun `Should return success when all internal methods return success`() = runTest {
        //Arrange
        coEvery {
            messageRepository.sendMessage(any())
        } returns NetworkResult.Success(fakeMessage.messageId)

        coEvery {
            conversationRepository.updateMetadataConversation(any())
        } returns NetworkResult.Success(Unit)

        coEvery {
            syncManager.updateConversationStatus(fakeMessage.conversationId, true)
        } returns Unit

        coEvery {
            syncManager.updateMessageStatus(fakeMessage.messageId, status = any())
        } returns Unit

        coEvery {
            pushNotificationRepository.pushNotification(any())
        } returns NetworkResult.Success(Unit)

        //Act
        val actual = useCase(
            senderId = fakeMessage.senderId,
            receiverId = fakeMessage.receiverId,
            conversationId = fakeMessage.conversationId,
            text = fakeMessage.content,
        )

        val expected = NetworkResult.Success(fakeMessage.messageId)

        //Assert
        Truth.assertThat(actual).isEqualTo(expected)
    }
}