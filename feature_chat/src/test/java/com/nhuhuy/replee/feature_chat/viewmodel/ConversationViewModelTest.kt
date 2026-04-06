package com.nhuhuy.replee.feature_chat.viewmodel

import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.usecase.GetCurrentAccountUseCase
import com.nhuhuy.core.domain.usecase.SearchAccountByEmailUseCase
import com.nhuhuy.replee.core.test.MainDispatcherRule
import com.nhuhuy.replee.feature_chat.domain.usecase.account.SetUserOnlineUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.account.UpdateCurrentAccountUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.conversation.GetSearchHistoryUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.conversation.LoadConversationUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.conversation.SaveConversationListUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.sync.SyncConversationUsersUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.sync.SyncConversationsUseCase
import com.nhuhuy.replee.feature_chat.presentation.conversation.ConversationViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ConversationViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var currentUserId: String
    private lateinit var setUserOnlineUseCase: SetUserOnlineUseCase
    private lateinit var getSearchHistoryUseCase: GetSearchHistoryUseCase
    private lateinit var syncConversationUseCase: SyncConversationsUseCase
    private lateinit var loadConversationUseCase: LoadConversationUseCase
    private lateinit var saveConversationListUseCase: SaveConversationListUseCase
    private lateinit var updateCurrentAccountUseCase: UpdateCurrentAccountUseCase
    private lateinit var getCurrentAccountUseCase: GetCurrentAccountUseCase
    private lateinit var searchAccountByEmailUseCase: SearchAccountByEmailUseCase
    private lateinit var syncConversationUsersUseCase: SyncConversationUsersUseCase

    @Before
    fun setUp() {
        currentUserId = "id_1"
        setUserOnlineUseCase = mockk()
        getSearchHistoryUseCase = mockk()
        syncConversationUseCase = mockk()
        loadConversationUseCase = mockk()
        saveConversationListUseCase = mockk()
        updateCurrentAccountUseCase = mockk()
        getCurrentAccountUseCase = mockk()
        searchAccountByEmailUseCase = mockk()
        syncConversationUsersUseCase = mockk()

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Test all methods run in init block`() = runTest {
        listOf("uid_1", "uid_2")
        val mockAccount = Account(id = currentUserId, name = "Huy", email = "huy@test.com")

        // Mock các Flow
        coEvery { getSearchHistoryUseCase(currentUserId) } returns flowOf(emptyList())
        coEvery { loadConversationUseCase(currentUserId) } returns flowOf(emptyList())
        coEvery { syncConversationUsersUseCase(currentUserId) } returns flowOf(Unit)

        // Mock các Suspend function trả về Result/Unit
        coEvery { updateCurrentAccountUseCase(any()) } returns NetworkResult.Success(currentUserId)
        coEvery { setUserOnlineUseCase(any()) } returns Unit
        coEvery { getCurrentAccountUseCase() } returns mockAccount
        coEvery { syncConversationUseCase(currentUserId, 20) } returns flowOf(Unit)

        ConversationViewModel(
            currentUserId = currentUserId,
            setUserOnlineUseCase = setUserOnlineUseCase,
            getSearchHistoryUseCase = getSearchHistoryUseCase,
            loadConversationUseCase = loadConversationUseCase,
            saveConversationListUseCase = saveConversationListUseCase,
            updateCurrentAccountUseCase = updateCurrentAccountUseCase,
            getCurrentAccountUseCase = getCurrentAccountUseCase,
            searchAccountByEmailUseCase = searchAccountByEmailUseCase,
            syncConversationUsersUseCase = syncConversationUsersUseCase,
            syncConversationUseCase = syncConversationUseCase
        )

        advanceUntilIdle()

        // --- THEN: Xác thực các hành vi ---
        coVerify {
            getSearchHistoryUseCase(currentUserId)
            loadConversationUseCase(currentUserId)
            updateCurrentAccountUseCase(currentUserId)
            setUserOnlineUseCase(currentUserId)
            getCurrentAccountUseCase()
            syncConversationUseCase(currentUserId, 20)
            syncConversationUsersUseCase(currentUserId)
        }
    }
}