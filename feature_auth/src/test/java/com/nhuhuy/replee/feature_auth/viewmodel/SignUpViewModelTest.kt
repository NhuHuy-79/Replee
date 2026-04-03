@file:OptIn(ExperimentalCoroutinesApi::class)

package com.nhuhuy.replee.feature_auth.viewmodel

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.common.base.ValidateResult
import com.nhuhuy.replee.core.common.utils.InputValidator
import com.nhuhuy.replee.core.data.mapper.toRemoteFailure
import com.nhuhuy.replee.core.test.MainDispatcherRule
import com.nhuhuy.replee.feature_auth.FakeParameters.Companion.fakeException
import com.nhuhuy.replee.feature_auth.domain.usecase.SignUpWithEmailUseCase
import com.nhuhuy.replee.feature_auth.presentation.sign_up.SignUpAction
import com.nhuhuy.replee.feature_auth.presentation.sign_up.SignUpEvent
import com.nhuhuy.replee.feature_auth.presentation.sign_up.SignUpViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SignUpViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var inputValidator: InputValidator
    private lateinit var signUpWithEmailUseCase: SignUpWithEmailUseCase

    private lateinit var viewModel: SignUpViewModel

    @Before
    fun setUp() {
        inputValidator = mockk(relaxed = true)
        signUpWithEmailUseCase = mockk(relaxed = true)

        viewModel = SignUpViewModel(
            inputValidator = inputValidator,
            signUpWithEmailUseCase = signUpWithEmailUseCase
        )
    }

    @Test
    fun `Initial state should be correct`() = runTest {
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.name.text).isEmpty()
            assertThat(state.email.text).isEmpty()
            assertThat(state.password.text).isEmpty()
            assertThat(state.confirmPassword.text).isEmpty()
            assertThat(state.showLoading).isFalse()
            assertThat(state.inputValid).isFalse()
        }
    }

    @Test
    fun `OnNameChange should update name`() = runTest {
        val name = "Test Name"
        viewModel.onAction(SignUpAction.OnNameChange(name))

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.name.text).isEqualTo(name)
            assertThat(state.name.valid).isTrue()
            assertThat(state.name.validateResult).isEqualTo(ValidateResult.Valid)
        }
    }

    @Test
    fun `OnEmailChange should update email and validate`() = runTest {
        val email = "test@example.com"
        every { inputValidator.validateEmail(email) } returns ValidateResult.Valid

        viewModel.onAction(SignUpAction.OnEmailChange(email))

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.email.text).isEqualTo(email)
            assertThat(state.email.valid).isTrue()
            assertThat(state.email.validateResult).isEqualTo(ValidateResult.Valid)
        }
    }

    @Test
    fun `OnPasswordChange should update password and validate`() = runTest {
        val password = "Password123"
        every { inputValidator.validatePassword(password) } returns ValidateResult.Valid

        viewModel.onAction(SignUpAction.OnPasswordChange(password))

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.password.text).isEqualTo(password)
            assertThat(state.password.valid).isTrue()
            assertThat(state.password.validateResult).isEqualTo(ValidateResult.Valid)
        }
    }

    @Test
    fun `OnConfirmPasswordChange should update confirmPassword and validate`() = runTest {
        val password = "Password123"
        val confirmPassword = "Password123"
        every { inputValidator.validatePassword(password) } returns ValidateResult.Valid
        every {
            inputValidator.isPasswordConfirmed(
                password,
                confirmPassword
            )
        } returns ValidateResult.Valid

        // Set initial password state first
        viewModel.onAction(SignUpAction.OnPasswordChange(password))

        viewModel.onAction(SignUpAction.OnConfirmPasswordChange(confirmPassword))

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.confirmPassword.text).isEqualTo(confirmPassword)
            assertThat(state.confirmPassword.valid).isTrue()
            assertThat(state.confirmPassword.validateResult).isEqualTo(ValidateResult.Valid)
        }
    }

    @Test
    fun `SignUp failure should emit Failure event`() = runTest {
        val name = "Test Name"
        val email = "test@example.com"
        val password = "Password123"
        val confirmPassword = "Password123"

        every { inputValidator.validateEmail(any()) } returns ValidateResult.Valid
        every { inputValidator.validatePassword(any()) } returns ValidateResult.Valid
        every { inputValidator.isPasswordConfirmed(any(), any()) } returns ValidateResult.Valid
        coEvery { signUpWithEmailUseCase(name, email, password) } returns NetworkResult.Failure(
            fakeException
        )

        viewModel.onAction(SignUpAction.OnNameChange(name))
        viewModel.onAction(SignUpAction.OnEmailChange(email))
        viewModel.onAction(SignUpAction.OnPasswordChange(password))
        viewModel.onAction(SignUpAction.OnConfirmPasswordChange(confirmPassword))

        viewModel.event.test {
            viewModel.onAction(SignUpAction.SignUp)

            val event = awaitItem()
            assertThat(event).isEqualTo(
                SignUpEvent.Failure(fakeException.toRemoteFailure())
            )
        }
    }

    @Test
    fun `NavigateBack action should emit NavigateBack event`() = runTest {
        viewModel.event.test {
            viewModel.onAction(SignUpAction.NavigateBack)
            assertThat(awaitItem()).isEqualTo(SignUpEvent.NavigateBack)
        }
    }
}