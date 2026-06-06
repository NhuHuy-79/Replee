package com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.input

import com.google.common.truth.Truth.assertThat
import com.nhuhuy.replee.core.common.di.ScopeHolder
import com.nhuhuy.replee.feature_chat.domain.usecase.file.SendFileMessageUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.file.ValidateFileSizeUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.SendMessageUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.metadata.UpdateTypingUseCase
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.ChatMediator
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.ChatMediatorState
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MessageInputViewModelTest {

    private lateinit var viewModel: MessageInputViewModel
    private val sendMessageUseCase = mockk<SendMessageUseCase>(relaxed = true)
    private val sendFileMessageUseCase = mockk<SendFileMessageUseCase>(relaxed = true)
    private val validateFileSizeUseCase = mockk<ValidateFileSizeUseCase>(relaxed = true)
    private val updateTypingUseCase = mockk<UpdateTypingUseCase>(relaxed = true)
    private val scopeHolder = mockk<ScopeHolder>(relaxed = true)
    private val mediator = mockk<ChatMediator>(relaxed = true)

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { scopeHolder.getOrCreateMediator<ChatMediator>(any(), any()) } returns mediator
        every { mediator.state } returns MutableStateFlow(
            ChatMediatorState(
                currentUserId = "me",
                otherUserId = "other"
            )
        )
        every { mediator.currentState } returns ChatMediatorState(
            currentUserId = "me",
            otherUserId = "other"
        )

        viewModel = MessageInputViewModel(
            sendMessageUseCase = sendMessageUseCase,
            sendFileMessageUseCase = sendFileMessageUseCase,
            validateFileSizeUseCase = validateFileSizeUseCase,
            updateTypingUseCase = updateTypingUseCase,
            scopeHolder = scopeHolder,
            chatScopeId = "test_scope"
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `When typing, should update input state and trigger typing usecase`() = runTest {
        // Act
        viewModel.onAction(MessageInputAction.OnMessageInputChange("Hello"))

        // Assert
        assertThat(viewModel.state.value.input).isEqualTo("Hello")
        coVerify {
            updateTypingUseCase(
                conversationId = any(),
                userId = "me",
                typing = true
            )
        }
    }

    @Test
    fun `When send button clicked, should clear input and call send message usecase`() = runTest {
        // Arrange
        viewModel.onAction(MessageInputChangeAction("Hello"))

        // Act
        viewModel.onAction(MessageInputAction.OnSendButtonClick)

        // Assert
        assertThat(viewModel.state.value.input).isEmpty()
        coVerify {
            sendMessageUseCase(
                repliedMessage = null,
                senderId = "me",
                receiverId = "other",
                conversationId = any(),
                text = "Hello"
            )
            mediator.removeSelectedMessage()
        }
    }

    private fun MessageInputChangeAction(text: String) =
        MessageInputAction.OnMessageInputChange(text)
}
