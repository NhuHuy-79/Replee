package com.nhuhuy.replee.feature_auth.viewmodel

import app.cash.turbine.test
import com.google.common.truth.Truth
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.data.model.ValidateResult
import com.nhuhuy.replee.core.common.utils.Validator
import com.nhuhuy.replee.core.test.DispatcherRuleTest
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import com.nhuhuy.replee.feature_auth.presentation.recover_password.RecoverPasswordAction
import com.nhuhuy.replee.feature_auth.presentation.recover_password.RecoverPasswordEvent
import com.nhuhuy.replee.feature_auth.presentation.recover_password.RecoverPasswordViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RecoverPasswordViewModelTest {
    @get: Rule
    val rule = DispatcherRuleTest()
    private lateinit var validator: Validator
    private lateinit var authRepository: AuthRepository
    private lateinit var recoverPasswordViewModel: RecoverPasswordViewModel

    @Before
    fun setUp(){
        validator = mockk()
        authRepository = mockk()
        recoverPasswordViewModel = RecoverPasswordViewModel(validator, authRepository)
    }

    @Test
    fun sendEmail_ShouldReturnSuccess() = runTest {
        coEvery {
            authRepository.sendRecoverPasswordEmail("email")
        } returns Resource.Success(Unit)

        every { validator.validateEmail("email") } returns ValidateResult.Valid

        recoverPasswordViewModel.onAction(RecoverPasswordAction.OnEmailChange("email"))
        recoverPasswordViewModel.event.test {
            recoverPasswordViewModel.onAction(RecoverPasswordAction.OnSubmit)
            val event = awaitItem()
            Truth.assertThat(event).isEqualTo(RecoverPasswordEvent.SendEmailSuccessfully)
        }
    }
}