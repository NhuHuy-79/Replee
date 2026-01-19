package com.nhuhuy.replee.feature_profile

import app.cash.turbine.test
import com.google.common.truth.Truth
import com.nhuhuy.replee.core.common.data.model.Account
import com.nhuhuy.replee.core.common.data.repository.AccountRepository
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.utils.Validator
import com.nhuhuy.replee.core.test.DispatcherRuleTest
import com.nhuhuy.replee.feature_profile.data.data_store.SettingDataStore
import com.nhuhuy.replee.feature_profile.domain.repository.ProfileRepository
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

    private lateinit var validator: Validator
    private lateinit var profileRepository: ProfileRepository
    private lateinit var accountRepository: AccountRepository
    private lateinit var dataStore: SettingDataStore
    private lateinit var viewModel: ProfileViewModel

    @get:Rule
    val rule = DispatcherRuleTest()

    @Before
    fun setUp(){
        validator = mockk(relaxed = true)
        profileRepository = mockk(relaxed = true)
        accountRepository = mockk(relaxed = true)
        dataStore = mockk(relaxed = true)
        viewModel = ProfileViewModel(validator, profileRepository, accountRepository, dataStore)
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
            accountRepository.getCurrentAccount()
        } returns Account()

        every {
            validator.validatePassword("old")
        } returns mockk(relaxed = true)

        every {
            validator.validateNewPassword("old", "new")
        } returns mockk(relaxed = true)

        coEvery {
            profileRepository.updateNewPassword("old", "new")
        } returns Resource.Success(Unit)

        viewModel.onAction(ProfileAction.OnOldPasswordChange("old"))
        viewModel.onAction(ProfileAction.OnNewPasswordChange("new"))
        viewModel.onAction(ProfileAction.OnUpdatePassword.Confirm)

        viewModel.event.test {
            val actual = awaitItem()
            Truth.assertThat(actual).isEqualTo(ProfileEvent.UpdatePassword.Success)
        }

    }
}