package com.nhuhuy.replee.feature_profile

import app.cash.turbine.test
import com.google.common.truth.Truth
import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.usecase.GetCurrentAccountUseCase
import com.nhuhuy.replee.core.common.utils.InputValidator
import com.nhuhuy.replee.core.test.DispatcherRuleTest
import com.nhuhuy.replee.feature_profile.data.data_store.SettingDataStore
import com.nhuhuy.replee.feature_profile.domain.usecase.LogOutUseCase
import com.nhuhuy.replee.feature_profile.domain.usecase.UpdatePasswordUseCase
import com.nhuhuy.replee.feature_profile.presentation.ProfileViewModel
import com.nhuhuy.replee.feature_profile.presentation.profile.state.Overlay
import com.nhuhuy.replee.feature_profile.presentation.profile.state.ProfileAction
import com.nhuhuy.replee.feature_profile.presentation.profile.state.ProfileEvent
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProfileViewModelTest {

    private lateinit var inputValidator: InputValidator
    private lateinit var updatePasswordUseCase: UpdatePasswordUseCase
    private lateinit var logOutUseCase: LogOutUseCase
    private lateinit var getCurrentAccountUseCase: GetCurrentAccountUseCase
    private lateinit var dataStore: SettingDataStore
    private lateinit var viewModel: ProfileViewModel

    @get:Rule
    val rule = DispatcherRuleTest()

    @Before
    fun setUp(){
        inputValidator = mockk(relaxed = true)
        updatePasswordUseCase = mockk(relaxed = true)
        logOutUseCase = mockk(relaxed = true)
        getCurrentAccountUseCase = mockk(relaxed = true)
        dataStore = mockk(relaxed = true)
        viewModel = ProfileViewModel(
            inputValidator,
            updatePasswordUseCase,
            logOutUseCase,
            getCurrentAccountUseCase,
            dataStore
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun returnDialogState_whenOpenNotificationDialog() = runTest {
        //This stateflow is made by stateIn function and it needs at least one collector (SharingStarted.WhileSubscribed).
        //Therefore, I have to create an empty collector with backgroundScope
        backgroundScope.launch {
            viewModel.state.test {
                viewModel.onAction(ProfileAction.OnNotificationClick.Dialog)

                advanceUntilIdle()

                val updated = awaitItem()
                Truth.assertThat(updated.overlay).isEqualTo(Overlay.NOTIFICATION)
            }
        }
    }

    @Test
    fun returnSuccessEvent_whenUpdatePassword() = runTest {
        coEvery {
            getCurrentAccountUseCase()
        } returns Account()

        every {
            inputValidator.validatePassword("old")
        } returns mockk(relaxed = true)

        every {
            inputValidator.validateNewPassword("old", "new")
        } returns mockk(relaxed = true)

        coEvery {
            updatePasswordUseCase("old", "new")
        } returns NetworkResult.Success(Unit)

        viewModel.onAction(ProfileAction.OnOldPasswordChange("old"))
        viewModel.onAction(ProfileAction.OnNewPasswordChange("new"))
        viewModel.onAction(ProfileAction.OnUpdatePassword.Confirm)

        viewModel.event.test {
            val actual = awaitItem()
            Truth.assertThat(actual).isEqualTo(ProfileEvent.UpdatePassword.Success)
        }

    }
}