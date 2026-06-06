package com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.background

import com.google.common.truth.Truth.assertThat
import com.nhuhuy.replee.core.common.di.ScopeHolder
import com.nhuhuy.replee.core.domain.usecase.GetAccountByIdUseCase
import com.nhuhuy.replee.core.sync.domain.usecase.message.SyncMessageUseCase
import com.nhuhuy.replee.feature_chat.data.NotificationManager
import com.nhuhuy.replee.feature_chat.domain.usecase.block.CheckBlockUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.block.ObserveOwnerIsBlockUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.block.UnblockUserUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.conversation.GetConversationUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.GetMessagePositionUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.metadata.GetReadTimeUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.metadata.GetTypingUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.metadata.UpdateReadTimeUseCase
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.ChatMediator
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.ChatMediatorState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatBackgroundViewModelTest {

    private lateinit var viewModel: ChatBackgroundViewModel
    private val scopeHolder = mockk<ScopeHolder>(relaxed = true)
    private val mediator = mockk<ChatMediator>(relaxed = true)

    private val updateReadTimeUseCase = mockk<UpdateReadTimeUseCase>(relaxed = true)
    private val syncMessageUseCase = mockk<SyncMessageUseCase>(relaxed = true)
    private val getAccountByIdUseCase = mockk<GetAccountByIdUseCase>(relaxed = true)
    private val checkBlockUseCase = mockk<CheckBlockUseCase>(relaxed = true)
    private val getConversationUseCase = mockk<GetConversationUseCase>(relaxed = true)
    private val getMessagePositionUseCase = mockk<GetMessagePositionUseCase>(relaxed = true)
    private val unblockUserUseCase = mockk<UnblockUserUseCase>(relaxed = true)
    private val notificationManager = mockk<NotificationManager>(relaxed = true)
    private val observeOwnerIsBlockUseCase = mockk<ObserveOwnerIsBlockUseCase>(relaxed = true)
    private val getReadTimeUseCase = mockk<GetReadTimeUseCase>(relaxed = true)
    private val getTypingUseCase = mockk<GetTypingUseCase>(relaxed = true)

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

        every { observeOwnerIsBlockUseCase(any(), any()) } returns flowOf(false)
        every { getReadTimeUseCase(any(), any()) } returns flowOf(0L)
        every { getTypingUseCase(any()) } returns flowOf(emptyList())
        every { syncMessageUseCase(any()) } returns flowOf(Unit)

        viewModel = ChatBackgroundViewModel(
            scopeId = "test_scope",
            otherUserId = "other",
            currentUserId = "me",
            anchorMessageId = null,
            scopeHolder = scopeHolder,
            updateReadTimeUseCase = updateReadTimeUseCase,
            syncMessageUseCase = syncMessageUseCase,
            getAccountByIdUseCase = getAccountByIdUseCase,
            checkBlockUseCase = checkBlockUseCase,
            getConversationUseCase = getConversationUseCase,
            getMessagePositionUseCase = getMessagePositionUseCase,
            unblockUserUseCase = unblockUserUseCase,
            notificationManager = notificationManager,
            observeOwnerIsBlockUseCase = observeOwnerIsBlockUseCase,
            getReadTimeUseCase = getReadTimeUseCase,
            getTypingUseCase = getTypingUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `When initialized, should have correct initial combine state`() = runTest {
        // Assert
        assertThat(viewModel.combineState.value.ownerIsBlock).isFalse()
        assertThat(viewModel.combineState.value.otherReadingTime).isEqualTo(0L)
        assertThat(viewModel.combineState.value.typingUserIds).isEmpty()
    }
}
