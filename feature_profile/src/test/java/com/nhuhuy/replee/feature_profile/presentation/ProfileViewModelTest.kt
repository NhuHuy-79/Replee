package com.nhuhuy.replee.feature_profile.presentation

import com.nhuhuy.replee.core.common.utils.InputValidator
import com.nhuhuy.replee.core.database.data_store.AppDataStore
import com.nhuhuy.replee.core.domain.usecase.GetCurrentAccountUseCase
import com.nhuhuy.replee.core.model.account.Account
import com.nhuhuy.replee.core.model.settings.NotificationMode
import com.nhuhuy.replee.core.model.settings.ThemeMode
import com.nhuhuy.replee.feature_profile.domain.usecase.LogOutUseCase
import com.nhuhuy.replee.feature_profile.domain.usecase.ObserveUploadAvatarUseCase
import com.nhuhuy.replee.feature_profile.domain.usecase.UpdatePasswordUseCase
import com.nhuhuy.replee.feature_profile.domain.usecase.UploadAvatarUseCase
import com.nhuhuy.replee.feature_profile.presentation.profile.state.ProfileAction
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private lateinit var viewModel: ProfileViewModel
    private val inputValidator = mockk<InputValidator>(relaxed = true)
    private val updatePasswordUseCase = mockk<UpdatePasswordUseCase>(relaxed = true)
    private val logOutUseCase = mockk<LogOutUseCase>(relaxed = true)
    private val getCurrentAccountUseCase = mockk<GetCurrentAccountUseCase>(relaxed = true)
    private val uploadAvatarUseCase = mockk<UploadAvatarUseCase>(relaxed = true)
    private val observeUploadAvatarUseCase = mockk<ObserveUploadAvatarUseCase>(relaxed = true)
    private val dataStore = mockk<AppDataStore>(relaxed = true)

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        coEvery { getCurrentAccountUseCase() } returns Account(id = "user123", name = "Test User")
        every { dataStore.observeTheme() } returns flowOf(ThemeMode.LIGHT)
        every { dataStore.observeNotification() } returns flowOf(NotificationMode.PRIVATE)

        viewModel = ProfileViewModel(
            inputValidator,
            updatePasswordUseCase,
            logOutUseCase,
            getCurrentAccountUseCase,
            uploadAvatarUseCase,
            observeUploadAvatarUseCase,
            dataStore
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `When LogOut action is called, it should trigger logOutUseCase`() = runTest {
        // Act
        viewModel.onAction(ProfileAction.OnLogOut)

        // Assert
        coVerify { logOutUseCase() }
    }
}
