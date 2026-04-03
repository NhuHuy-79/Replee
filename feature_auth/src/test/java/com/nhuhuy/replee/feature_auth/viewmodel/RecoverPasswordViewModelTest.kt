package com.nhuhuy.replee.feature_auth.viewmodel

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.common.base.ValidateResult
import com.nhuhuy.replee.core.common.utils.InputValidator
import com.nhuhuy.replee.core.data.mapper.toRemoteFailure
import com.nhuhuy.replee.core.test.MainDispatcherRule
import com.nhuhuy.replee.feature_auth.FakeParameters.Companion.fakeException
import com.nhuhuy.replee.feature_auth.domain.usecase.SendRecoveryEmailUseCase
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

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var inputValidator: InputValidator
    private lateinit var sendRecoveryEmailUseCase: SendRecoveryEmailUseCase

    private lateinit var viewModel: RecoverPasswordViewModel

    @Before
    fun setUp() {
        inputValidator = mockk(relaxed = true)
        sendRecoveryEmailUseCase = mockk(relaxed = true)

        viewModel = RecoverPasswordViewModel(
            inputValidator = inputValidator,
            sendRecoveryEmailUseCase = sendRecoveryEmailUseCase
        )
    }

    @Test
    fun `Initial state should be correct`() = runTest {
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.email.text).isEmpty()
            assertThat(state.showLoading).isFalse()
            assertThat(state.inputValid).isFalse()
        }
    }

    @Test
    fun `OnEmailChange should update email and validate`() = runTest {
        val email = "test@example.com"
        every { inputValidator.validateEmail(email) } returns ValidateResult.Valid

        viewModel.onAction(RecoverPasswordAction.OnEmailChange(email))

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.email.text).isEqualTo(email)
            assertThat(state.email.valid).isTrue()
            assertThat(state.email.validateResult).isEqualTo(ValidateResult.Valid)
        }
    }

    @Test
    fun `OnSubmit success should emit SendEmailSuccessfully event`() = runTest {
        val email = "test@example.com"

        every { inputValidator.validateEmail(any()) } returns ValidateResult.Valid
        coEvery { sendRecoveryEmailUseCase(email) } returns NetworkResult.Success(Unit)

        // Set initial valid state
        viewModel.onAction(RecoverPasswordAction.OnEmailChange(email))

        viewModel.event.test {
            viewModel.onAction(RecoverPasswordAction.OnSubmit)
            assertThat(awaitItem()).isEqualTo(RecoverPasswordEvent.SendEmailSuccessfully)
        }
    }

    @Test
    fun `OnSubmit failure should emit Failure event`() = runTest {
        val email = "test@example.com"

        every { inputValidator.validateEmail(any()) } returns ValidateResult.Valid
        coEvery { sendRecoveryEmailUseCase(email) } returns NetworkResult.Failure(fakeException)

        // Set initial valid state
        viewModel.onAction(RecoverPasswordAction.OnEmailChange(email))

        viewModel.event.test {
            viewModel.onAction(RecoverPasswordAction.OnSubmit)
            assertThat(awaitItem()).isEqualTo(RecoverPasswordEvent.Failure(fakeException.toRemoteFailure()))
        }
    }

    @Test
    fun `OnBack action should emit NavigateBack event`() = runTest {
        viewModel.event.test {
            viewModel.onAction(RecoverPasswordAction.OnBack)
            assertThat(awaitItem()).isEqualTo(RecoverPasswordEvent.NavigateBack)
        }
    }
}