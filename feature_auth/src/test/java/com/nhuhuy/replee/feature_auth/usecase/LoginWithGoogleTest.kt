package com.nhuhuy.replee.feature_auth.usecase

import com.google.common.truth.Truth
import com.nhuhuy.core.domain.SessionManager
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.repository.AccountRepository
import com.nhuhuy.replee.feature_auth.FakeParameters.Companion.FAKE_TOKEN
import com.nhuhuy.replee.feature_auth.FakeParameters.Companion.fakeAccount
import com.nhuhuy.replee.feature_auth.FakeParameters.Companion.fakeException
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import com.nhuhuy.replee.feature_auth.domain.usecase.SignInWithGoogleUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Orchestrates multiple operations to return a unified [NetworkResult].
 * * Logic:
 * - Returns [NetworkResult.Success] if **all** internal operations succeed.
 * - Returns [NetworkResult.Failure] as soon as **any** operation fails.
 *
 * @return A [NetworkResult] representing the overall outcome of the operations.
 */
class SignInWithGoogleTest {
    private lateinit var authRepository: AuthRepository
    private lateinit var accountRepository: AccountRepository
    private lateinit var sessionManager: SessionManager

    private lateinit var useCase: SignInWithGoogleUseCase

    private val FAKE_ID_TOKEN = "fake_id_token_google"

    @Before
    fun setUp() {
        authRepository = mockk(relaxed = true)
        accountRepository = mockk(relaxed = true)
        sessionManager = mockk(relaxed = true)

        useCase = SignInWithGoogleUseCase(
            authRepository = authRepository,
            accountRepository = accountRepository,
            sessionManager = sessionManager
        )
    }

    @Test
    fun `Should return success with account when all steps are successful`() = runTest {
        // Arrange
        coEvery {
            authRepository.signInWithGoogle(FAKE_ID_TOKEN)
        } returns NetworkResult.Success(fakeAccount)

        coEvery {
            sessionManager.getCurrentDeviceToken()
        } returns NetworkResult.Success(FAKE_TOKEN)

        coEvery {
            accountRepository.createAccount(FAKE_TOKEN, fakeAccount)
        } returns NetworkResult.Success(fakeAccount)

        coEvery {
            authRepository.provideAuthenticateToken()
        } returns NetworkResult.Success(FAKE_TOKEN)

        // Act
        val result = useCase(FAKE_ID_TOKEN)
        val expected = NetworkResult.Success(fakeAccount)

        // Assert
        Truth.assertThat(result).isEqualTo(expected)

        coVerify {
            sessionManager.refreshAuthenticationToken(FAKE_TOKEN)
        }
    }

    @Test
    fun `Should return failure and logout when signInWithGoogle fails`() = runTest {
        // Arrange
        coEvery {
            authRepository.signInWithGoogle(FAKE_ID_TOKEN)
        } returns NetworkResult.Failure(fakeException)

        // Act
        val result = useCase(FAKE_ID_TOKEN)
        val expected = NetworkResult.Failure(fakeException)

        // Assert
        Truth.assertThat(result).isEqualTo(expected)

        coVerify { sessionManager.logout() }
        coVerify(exactly = 0) { accountRepository.createAccount(any(), any()) }
    }

    @Test
    fun `Should return failure and logout when getCurrentDeviceToken fails`() = runTest {
        // Arrange
        coEvery {
            authRepository.signInWithGoogle(FAKE_ID_TOKEN)
        } returns NetworkResult.Success(fakeAccount)

        coEvery {
            sessionManager.getCurrentDeviceToken()
        } returns NetworkResult.Failure(fakeException)

        // Act
        val result = useCase(FAKE_ID_TOKEN)
        val expected = NetworkResult.Failure(fakeException)

        // Assert
        Truth.assertThat(result).isEqualTo(expected)

        coVerify { sessionManager.logout() }
        coVerify(exactly = 0) { accountRepository.createAccount(any(), any()) }
    }

    @Test
    fun `Should return failure and logout when createAccount fails`() = runTest {
        // Arrange
        coEvery {
            authRepository.signInWithGoogle(FAKE_ID_TOKEN)
        } returns NetworkResult.Success(fakeAccount)

        coEvery {
            sessionManager.getCurrentDeviceToken()
        } returns NetworkResult.Success(FAKE_TOKEN)

        coEvery {
            accountRepository.createAccount(FAKE_TOKEN, fakeAccount)
        } returns NetworkResult.Failure(fakeException)

        // Act
        val result = useCase(FAKE_ID_TOKEN)
        val expected = NetworkResult.Failure(fakeException)

        // Assert
        Truth.assertThat(result).isEqualTo(expected)

        coVerify { sessionManager.logout() }
    }
}