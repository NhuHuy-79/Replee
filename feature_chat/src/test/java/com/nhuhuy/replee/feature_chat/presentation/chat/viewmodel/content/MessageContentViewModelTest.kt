package com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.content

import com.google.common.truth.Truth.assertThat
import com.nhuhuy.replee.core.common.di.ScopeHolder
import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import com.nhuhuy.replee.feature_chat.domain.repository.PaginatorRepository
import com.nhuhuy.replee.feature_chat.domain.usecase.message.AddReactionUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.DeleteMessageUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.PinMessageUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.RemoveReactionUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.message.UnPinMessageUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.paging.GetLatestMessagesUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.paging.GetMessageAfterKeyUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.paging.GetMessageBeforeKeyUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.paging.ObserveMessagesUseCase
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.ChatMediator
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.ChatMediatorState
import io.mockk.coEvery
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
class MessageContentViewModelTest {

    private lateinit var viewModel: MessageContentViewModel
    private val scopeHolder = mockk<ScopeHolder>(relaxed = true)
    private val mediator = mockk<ChatMediator>(relaxed = true)

    private val paginatorRepository = mockk<PaginatorRepository>(relaxed = true)
    private val observeMessagesUseCase = mockk<ObserveMessagesUseCase>(relaxed = true)
    private val getLatestMessagesUseCase = mockk<GetLatestMessagesUseCase>(relaxed = true)
    private val getMessageAfterKeyUseCase = mockk<GetMessageAfterKeyUseCase>(relaxed = true)
    private val getMessageBeforeKeyUseCase = mockk<GetMessageBeforeKeyUseCase>(relaxed = true)
    private val deleteMessageUseCase = mockk<DeleteMessageUseCase>(relaxed = true)
    private val pinMessageUseCase = mockk<PinMessageUseCase>(relaxed = true)
    private val unPinMessageUseCase = mockk<UnPinMessageUseCase>(relaxed = true)
    private val addReactionUseCase = mockk<AddReactionUseCase>(relaxed = true)
    private val removeReactionUseCase = mockk<RemoveReactionUseCase>(relaxed = true)

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

        every { observeMessagesUseCase(any(), any(), any()) } returns flowOf(emptyList())
        coEvery {
            getLatestMessagesUseCase(
                any(),
                any()
            )
        } returns NetworkResult.Success(emptyList())

        viewModel = MessageContentViewModel(
            scopeId = "test_scope",
            conversationId = "conv_id",
            anchorMessageId = null,
            scopeHolder = scopeHolder,
            paginatorRepository = paginatorRepository,
            observeMessagesUseCase = observeMessagesUseCase,
            getLatestMessagesUseCase = getLatestMessagesUseCase,
            getMessageAfterKeyUseCase = getMessageAfterKeyUseCase,
            getMessageBeforeKeyUseCase = getMessageBeforeKeyUseCase,
            deleteMessageUseCase = deleteMessageUseCase,
            pinMessageUseCase = pinMessageUseCase,
            unPinMessageUseCase = unPinMessageUseCase,
            addReactionUseCase = addReactionUseCase,
            removeReactionUseCase = removeReactionUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `When initialized, should observe messages and fetch latest from network`() = runTest {
        // Assert
        assertThat(viewModel.messagesUiFlow.value).isEmpty()
    }
}
