package com.nhuhuy.replee.feature_auth.viewmodel

import app.cash.turbine.test
import com.google.common.truth.Truth
import com.nhuhuy.replee.core.common.base.ValidateResult
import com.nhuhuy.replee.core.common.utils.InputValidator
import com.nhuhuy.replee.core.test.DispatcherRuleTest
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import com.nhuhuy.replee.feature_auth.presentation.recover_password.RecoverPasswordAction
import com.nhuhuy.replee.feature_auth.presentation.recover_password.RecoverPasswordEvent
import com.nhuhuy.replee.feature_auth.presentation.recover_password.RecoverPasswordViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RecoverPasswordViewModelTest {
    @get: Rule
    val rule = DispatcherRuleTest()
    private lateinit var inputValidator: InputValidator
    private lateinit var authRepository: AuthRepository
    private lateinit var recoverPasswordViewModel: RecoverPasswordViewModel

    @Before
    fun setUp(){
        inputValidator = mockk()
        authRepository = mockk()

    }

    @Test
    fun sendEmail_ShouldReturnSuccess() = runTest {

        every { inputValidator.validateEmail("email") } returns ValidateResult.Valid

        recoverPasswordViewModel.onAction(RecoverPasswordAction.OnEmailChange("email"))
        recoverPasswordViewModel.event.test {
            recoverPasswordViewModel.onAction(RecoverPasswordAction.OnSubmit)
            val event = awaitItem()
            Truth.assertThat(event).isEqualTo(RecoverPasswordEvent.SendEmailSuccessfully)
        }
    }
}