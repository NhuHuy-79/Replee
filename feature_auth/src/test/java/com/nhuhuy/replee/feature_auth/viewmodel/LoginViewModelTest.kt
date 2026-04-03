package com.nhuhuy.replee.feature_auth.viewmodel

import app.cash.turbine.test
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.common.base.ValidateResult
import com.nhuhuy.replee.core.common.utils.InputValidator
import com.nhuhuy.replee.core.test.MainDispatcherRule
import com.nhuhuy.replee.feature_auth.data.model.GoogleCredentialResult
import com.nhuhuy.replee.feature_auth.domain.usecase.LoginWithEmailUseCase
import com.nhuhuy.replee.feature_auth.domain.usecase.SignInWithGoogleUseCase
import com.nhuhuy.replee.feature_auth.presentation.login.LoginAction
import com.nhuhuy.replee.feature_auth.presentation.login.LoginEvent
import com.nhuhuy.replee.feature_auth.presentation.login.LoginViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: LoginViewModel
    private lateinit var inputValidator: InputValidator
    private lateinit var loginWithEmailUseCase: LoginWithEmailUseCase
    private lateinit var signInWithGoogleUseCase: SignInWithGoogleUseCase

    @Before
    fun setUp() {
        inputValidator = mockk(relaxed = true)
        loginWithEmailUseCase = mockk(relaxed = true)
        signInWithGoogleUseCase = mockk(relaxed = true)

        viewModel = LoginViewModel(
            inputValidator,
            loginWithEmailUseCase,
            signInWithGoogleUseCase
        )
    }

    @Test
    fun `Should update email state and validate when OnEmailChanged action is triggered`() =
        runTest {
            // Arrange
            val email = "test@replee.com"
            every { inputValidator.validateEmail(email) } returns ValidateResult.Valid

            // Act
            viewModel.onAction(LoginAction.OnEmailChanged(email))

            // Assert
            val currentState = viewModel.state.value
            assertEquals(email, currentState.email.text)
            assertTrue(currentState.email.validateResult is ValidateResult.Valid)
        }

    @Test
    fun `Should navigate to home when login with email is successful`() = runTest {
        // Arrange
        val email = "test@replee.com"
        val password = "password123"
        val fakeUid = "user_123"

        viewModel.onAction(LoginAction.OnEmailChanged(email))
        viewModel.onAction(LoginAction.OnPasswordChanged(password))

        coEvery { loginWithEmailUseCase(email, password) } returns NetworkResult.Success(fakeUid)

        // Act & Assert (Dùng Turbine để bắt Event)
        viewModel.event.test {
            viewModel.onAction(LoginAction.OnLoginWithEmail)

            val event = awaitItem()
            assertTrue(event is LoginEvent.NavigateToHome)
            assertEquals(fakeUid, (event as LoginEvent.NavigateToHome).uid)
        }
    }

    @Test
    fun `Should show failure event when login with email fails`() = runTest {
        // Arrange
        coEvery {
            loginWithEmailUseCase(
                any(),
                any()
            )
        } returns NetworkResult.Failure(Exception("Login Failed"))

        // Act & Assert
        viewModel.event.test {
            viewModel.onAction(LoginAction.OnLoginWithEmail)

            val event = awaitItem()
            assertTrue(event is LoginEvent.Failure)
        }
    }

    @Test
    fun `Should navigate to home when Google sign in is successful`() = runTest {
        // Arrange
        val idToken = "google_token"
        val googleResult = GoogleCredentialResult.Success(idToken = idToken)
        val fakeAccount = mockk<com.nhuhuy.core.domain.model.Account> {
            every { id } returns "google_user_id"
        }

        coEvery { signInWithGoogleUseCase(idToken) } returns NetworkResult.Success(fakeAccount)

        // Act & Assert
        viewModel.event.test {
            viewModel.onAction(LoginAction.OnLoginWithGoogle(googleResult))

            val event = awaitItem()
            assertTrue(event is LoginEvent.NavigateToHome)
            assertEquals("google_user_id", (event as LoginEvent.NavigateToHome).uid)
        }
    }

    @Test
    fun `Should show error snack bar when Google credential result is error`() = runTest {
        // Arrange
        val googleResult = GoogleCredentialResult.NoCredential

        // Act & Assert
        viewModel.event.test {
            viewModel.onAction(LoginAction.OnLoginWithGoogle(googleResult))

            val event = awaitItem()
            assertTrue(event is LoginEvent.GoogleErrorSnackBar)
        }
    }
}