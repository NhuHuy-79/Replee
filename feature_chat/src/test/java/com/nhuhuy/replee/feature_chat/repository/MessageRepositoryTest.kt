package com.nhuhuy.replee.feature_chat.repository

import com.google.common.truth.Truth
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.database.CoreDatabase
import com.nhuhuy.replee.core.network.data_source.UploadFileService
import com.nhuhuy.replee.core.test.DispatcherRuleTest
import com.nhuhuy.replee.feature_chat.data.mapper.toMessageDTO
import com.nhuhuy.replee.feature_chat.data.mapper.toMessageEntity
import com.nhuhuy.replee.feature_chat.data.model.network.ConversationDTO
import com.nhuhuy.replee.feature_chat.data.repository.MessageRepositoryImp
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationNetworkDataSource
import com.nhuhuy.replee.feature_chat.data.source.message.MessageLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.message.MessageNetworkDataSource
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.domain.model.MessageStatus
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MessageRepositoryTest {
    @get:Rule
    val main = DispatcherRuleTest()
    private lateinit var ioDispatcher: CoroutineDispatcher
    private lateinit var coreDatabase: CoreDatabase
    private lateinit var uploadFileService: UploadFileService
    private lateinit var messageNetworkDataSource: MessageNetworkDataSource
    private lateinit var messageLocalDatabase: MessageLocalDataSource
    private lateinit var conversationLocalDataSource: ConversationLocalDataSource
    private lateinit var conversationNetworkDataSource: ConversationNetworkDataSource

    private lateinit var repositoryImp: MessageRepositoryImp

    private val fakeMessage = Message(
        conversationId = "conversationId",
        messageId = "messageId",
        senderId = "senderId",
        receiverId = "receiverId",
        content = "content",
        seen = false,
        status = MessageStatus.PENDING,
    )

    private val fakeConversation = ConversationDTO(
        id = "conversationId"
    )

    @Before
    fun setUp() {
        ioDispatcher = StandardTestDispatcher(TestScope().testScheduler)
        coreDatabase = mockk(relaxed = true)
        uploadFileService = mockk(relaxed = true)
        messageLocalDatabase = mockk(relaxed = true)
        messageNetworkDataSource = mockk(relaxed = true)
        conversationNetworkDataSource = mockk(relaxed = true)
        conversationLocalDataSource = mockk(relaxed = true)

        repositoryImp = MessageRepositoryImp(
            ioDispatcher = ioDispatcher,
            coreDatabase = coreDatabase,
            messageLocalDataSource = messageLocalDatabase,
            messageNetworkDataSource = messageNetworkDataSource,
            conversationLocalDataSource = conversationLocalDataSource,
            conversationNetworkDataSource = conversationNetworkDataSource
        )
    }


    @Test
    fun `send text message when there is no exception and nullable data`() = runTest {
        //Arrange
        val entity = fakeMessage.toMessageEntity()
        val dto = fakeMessage.toMessageDTO()
        coEvery {
            messageLocalDatabase.upsertMessage(entity)
        } just Runs

        coEvery {
            conversationLocalDataSource.updateLastMessage(entity)
        } just Runs

        coEvery {
            messageNetworkDataSource.sendMessage(dto)
        } just Runs

        coEvery {
            conversationNetworkDataSource.fetchConversationById(fakeMessage.conversationId)
        } returns fakeConversation

        coEvery {
            conversationNetworkDataSource.updateLastMessage(dto, fakeConversation)
        } just Runs

        //Act
        val result = repositoryImp.sendMessage(fakeMessage)

        //Assert
        Truth.assertThat(result).isEqualTo(NetworkResult.Success(fakeMessage.messageId))

        coVerify {
            messageLocalDatabase.upsertMessage(entity)
            conversationLocalDataSource.updateLastMessage(entity)
            messageNetworkDataSource.sendMessage(dto)
            conversationNetworkDataSource.fetchConversationById(fakeMessage.conversationId)
            conversationNetworkDataSource.updateLastMessage(dto, fakeConversation)
        }
    }

    @Test
    fun `send message when cannot fetch conversationDTO to update network`() = runTest {
        //Arrange
        val entity = fakeMessage.toMessageEntity()
        val dto = fakeMessage.toMessageDTO()
        coEvery {
            messageLocalDatabase.upsertMessage(entity)
        } just Runs

        coEvery {
            conversationLocalDataSource.updateLastMessage(entity)
        } just Runs

        coEvery {
            messageNetworkDataSource.sendMessage(dto)
        } just Runs

        coEvery {
            conversationNetworkDataSource.fetchConversationById(fakeMessage.conversationId)
        } returns null


        //Act
        val result = repositoryImp.sendMessage(fakeMessage)

        //Assert
        Truth.assertThat(result).isEqualTo(NetworkResult.Success(fakeMessage.messageId))

        coVerify {
            messageLocalDatabase.upsertMessage(entity)
            conversationLocalDataSource.updateLastMessage(entity)
            messageNetworkDataSource.sendMessage(dto)
            conversationNetworkDataSource.fetchConversationById(fakeMessage.conversationId)
        }
    }


}