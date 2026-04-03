package com.nhuhuy.replee.feature_profile.viewmodel

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.usecase.GetCurrentAccountUseCase
import com.nhuhuy.replee.core.common.base.ValidateResult
import com.nhuhuy.replee.core.common.utils.InputValidator
import com.nhuhuy.replee.core.data.data_store.AppDataStore
import com.nhuhuy.replee.core.data.data_store.NotificationMode
import com.nhuhuy.replee.core.data.data_store.ThemeMode
import com.nhuhuy.replee.core.test.MainDispatcherRule
import com.nhuhuy.replee.feature_profile.FakeParameters.Companion.fakeAccount
import com.nhuhuy.replee.feature_profile.FakeParameters.Companion.fakeException
import com.nhuhuy.replee.feature_profile.domain.usecase.LogOutUseCase
import com.nhuhuy.replee.feature_profile.domain.usecase.ObserveUploadAvatarUseCase
import com.nhuhuy.replee.feature_profile.domain.usecase.UpdatePasswordUseCase
import com.nhuhuy.replee.feature_profile.domain.usecase.UploadAvatarUseCase
import com.nhuhuy.replee.feature_profile.presentation.ProfileViewModel
import com.nhuhuy.replee.feature_profile.presentation.profile.state.Overlay
import com.nhuhuy.replee.feature_profile.presentation.profile.state.ProfileAction
import com.nhuhuy.replee.feature_profile.presentation.profile.state.ProfileEvent
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var inputValidator: InputValidator
    private lateinit var updatePasswordUseCase: UpdatePasswordUseCase
    private lateinit var logOutUseCase: LogOutUseCase
    private lateinit var getCurrentAccountUseCase: GetCurrentAccountUseCase
    private lateinit var uploadAvatarUseCase: UploadAvatarUseCase
    private lateinit var observeUploadAvatarUseCase: ObserveUploadAvatarUseCase
    private lateinit var dataStore: AppDataStore

    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        inputValidator = mockk(relaxed = true)
        updatePasswordUseCase = mockk(relaxed = true)
        logOutUseCase = mockk(relaxed = true)
        getCurrentAccountUseCase = mockk(relaxed = true)
        uploadAvatarUseCase = mockk(relaxed = true)
        observeUploadAvatarUseCase = mockk(relaxed = true)
        dataStore = mockk(relaxed = true)

        coEvery { getCurrentAccountUseCase() } returns fakeAccount
        every { dataStore.observeTheme() } returns flowOf(ThemeMode.DEFAULT)
        every { dataStore.observeNotification() } returns flowOf(NotificationMode.NONE)

        viewModel = ProfileViewModel(
            inputValidator = inputValidator,
            updatePasswordUseCase = updatePasswordUseCase,
            logOutUseCase = logOutUseCase,
            getCurrentAccountUseCase = getCurrentAccountUseCase,
            uploadAvatarUseCase = uploadAvatarUseCase,
            observeUploadAvatarUseCase = observeUploadAvatarUseCase,
            dataStore = dataStore
        )
    }

    @Test
    fun `Initialization should load account and observe settings`() = runTest {
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.account).isEqualTo(fakeAccount)
            assertThat(state.darkMode).isEqualTo(ThemeMode.DEFAULT)
            assertThat(state.notification).isEqualTo(NotificationMode.NONE)
        }
    }

    @Test
    fun `OnLogOut action should call logOutUseCase and emit GoToSignIn event`() = runTest {
        viewModel.event.test {
            viewModel.onAction(ProfileAction.OnLogOut)

            coVerify { logOutUseCase() }
            assertThat(awaitItem()).isEqualTo(ProfileEvent.GoToSignIn)
        }
    }

    @Test
    fun `OnUpdatePassword Confirm should call use case and handle success`() = runTest {
        // Arrange
        val oldPass = "oldPassword123"
        val newPass = "newPassword123"

        every { inputValidator.validatePassword(any()) } returns ValidateResult.Valid
        every { inputValidator.validateNewPassword(any(), any()) } returns ValidateResult.Valid
        coEvery { updatePasswordUseCase(any(), any()) } returns NetworkResult.Success(Unit)

        viewModel.onAction(ProfileAction.OnOldPasswordChange(oldPass))
        viewModel.onAction(ProfileAction.OnNewPasswordChange(newPass))

        viewModel.event.test {
            // Act
            viewModel.onAction(ProfileAction.OnUpdatePassword.Confirm)

            // Assert
            coVerify { updatePasswordUseCase(oldPass, newPass) }
            assertThat(awaitItem()).isEqualTo(ProfileEvent.UpdatePassword.Success)

            viewModel.state.test {
                val state = awaitItem()
                assertThat(state.overlay).isEqualTo(Overlay.NONE)
            }
        }
    }

    @Test
    fun `OnUpdatePassword Confirm should handle failure`() = runTest {
        // Arrange
        coEvery { updatePasswordUseCase(any(), any()) } returns NetworkResult.Failure(fakeException)

        viewModel.event.test {
            // Act
            viewModel.onAction(ProfileAction.OnUpdatePassword.Confirm)

            // Assert
            val event = awaitItem()
            assertThat(event).isInstanceOf(ProfileEvent.UpdatePassword.Failure::class.java)
        }
    }

    @Test
    fun `OnDarkModeClick Dialog should update overlay state`() = runTest {
        viewModel.state.test {
            // Initial state
            assertThat(awaitItem().overlay).isEqualTo(Overlay.NONE)

            // Act
            viewModel.onAction(ProfileAction.OnDarkModeClick.Dialog)

            // Assert
            assertThat(awaitItem().overlay).isEqualTo(Overlay.THEME)
        }
    }

    @Test
    fun `OnDarkModeClick Select should save theme and close overlay`() = runTest {
        val themeOption = ThemeMode.DARK

        viewModel.onAction(ProfileAction.OnDarkModeClick.Select(themeOption))

        coVerify { dataStore.saveThemeMode(themeOption) }
        viewModel.state.test {
            assertThat(awaitItem().overlay).isEqualTo(Overlay.NONE)
        }
    }
}
