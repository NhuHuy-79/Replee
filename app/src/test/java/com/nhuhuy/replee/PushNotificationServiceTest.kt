@file:OptIn(ExperimentalCoroutinesApi::class)

package com.nhuhuy.replee

import com.google.firebase.messaging.RemoteMessage
import com.nhuhuy.core.domain.SessionManager
import com.nhuhuy.core.domain.usecase.UpdateDeviceTokenUseCase
import com.nhuhuy.replee.core.network.api.fcm.ContentType
import com.nhuhuy.replee.core.network.api.fcm.NotificationResponse
import com.nhuhuy.replee.core.test.MainDispatcherRule
import com.nhuhuy.replee.feature_chat.data.worker.WorkerScheduler
import com.nhuhuy.replee.feature_chat.domain.model.converastion.Conversation
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import com.nhuhuy.replee.notification.NotificationParser
import com.nhuhuy.replee.service.PushNotificationService
import com.nhuhuy.replee.service.ServiceNotifier
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PushNotificationServiceTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var updateDeviceTokenUseCase: UpdateDeviceTokenUseCase
    private lateinit var conversationRepository: ConversationRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var serviceNotifier: ServiceNotifier
    private lateinit var workerScheduler: WorkerScheduler
    private lateinit var notificationParser: NotificationParser

    private lateinit var service: PushNotificationService

    private val fakeResponse = NotificationResponse(
        conversationId = "123",
        senderId = "120",
        receiverId = "123",
        messageId = "123",
        type = ContentType.PLAIN_TEXT,
    )

    @Before
    fun setUp() {
        updateDeviceTokenUseCase = mockk(relaxed = true)
        serviceNotifier = mockk(relaxed = true)
        workerScheduler = mockk(relaxed = true)
        notificationParser = mockk(relaxed = true)
        sessionManager = mockk(relaxed = true)
        conversationRepository = mockk(relaxed = true)
        service = PushNotificationService().apply {
            this.updateDeviceTokenUseCase =
                this@PushNotificationServiceTest.updateDeviceTokenUseCase
            this.serviceNotifier = this@PushNotificationServiceTest.serviceNotifier
            this.workerScheduler = this@PushNotificationServiceTest.workerScheduler
            this.notificationParser = this@PushNotificationServiceTest.notificationParser
            this.dispatcher = StandardTestDispatcher()
            this.sessionManager = this@PushNotificationServiceTest.sessionManager
            this.conversationRepository = this@PushNotificationServiceTest.conversationRepository
        }
    }

    @Test
    fun `Should update token when new token is received`() = runTest {
        service.onNewToken("123")
        advanceUntilIdle()
        coVerify {
            updateDeviceTokenUseCase("123")
        }
    }

    @Test
    fun `Should show notification when new message is received and notification body is not null`() =
        runTest {
            val remoteMessage = mockk<RemoteMessage>()

            coEvery { conversationRepository.getConversationById(fakeResponse.conversationId) } returns Conversation().copy(
                id = fakeResponse.conversationId,
                muted = false
            )
            every { sessionManager.getUserIdOrNull() } returns "123"
            every { notificationParser.getNotificationBody(remoteMessage) } returns fakeResponse
            every { workerScheduler.scheduleSaveMessageWorker(fakeResponse.conversationId) } returns Unit
            coEvery { serviceNotifier.showConversationNotification(fakeResponse) } returns Unit

            service.onMessageReceived(remoteMessage)
            advanceUntilIdle()

            coVerify {
                sessionManager.getUserIdOrNull()
                notificationParser.getNotificationBody(remoteMessage)
                workerScheduler.scheduleSaveMessageWorker(fakeResponse.conversationId)
                conversationRepository.getConversationById(fakeResponse.conversationId)
                serviceNotifier.showConversationNotification(fakeResponse)
            }
        }

    @Test
    fun `Should show notification when new message is received and notification body is null`() =
        runTest {
            val remoteMessage = mockk<RemoteMessage>()
            coEvery { conversationRepository.getConversationById(any()) } returns mockk<Conversation>()
            every { sessionManager.getUserIdOrNull() } returns "12"
            every { notificationParser.getNotificationBody(remoteMessage) } returns null
            service.onMessageReceived(remoteMessage)

            advanceUntilIdle()

            coVerify {
                sessionManager.getUserIdOrNull()
                notificationParser.getNotificationBody(remoteMessage)
            }
        }

}