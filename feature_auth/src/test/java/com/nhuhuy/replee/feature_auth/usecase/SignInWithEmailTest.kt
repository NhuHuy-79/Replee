package com.nhuhuy.replee.feature_auth.usecase

import com.google.common.truth.Truth
import com.nhuhuy.core.domain.SessionManager
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.repository.AccountRepository
import com.nhuhuy.replee.feature_auth.FakeParameters.Companion.FAKE_TOKEN
import com.nhuhuy.replee.feature_auth.FakeParameters.Companion.fakeAccount
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import com.nhuhuy.replee.feature_auth.domain.usecase.SignUpWithEmailUseCase
import io.mockk.coEvery
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
class SignInWithEmailTest {
    private lateinit var authRepository: AuthRepository
    private lateinit var accountRepository: AccountRepository
    private lateinit var sessionManager: SessionManager

    private lateinit var useCase: SignUpWithEmailUseCase

    @Before
    fun setUp() {
        authRepository = mockk(relaxed = true)
        accountRepository = mockk(relaxed = true)
        sessionManager = mockk(relaxed = true)

        useCase = SignUpWithEmailUseCase(
            authRepository = authRepository,
            accountRepository = accountRepository,
            sessionManager = sessionManager
        )
    }

    @Test
    fun `Should return success with user id when all methods return success`() = runTest {
        //Arrange
        coEvery {
            authRepository.signUpWithEmail(fakeAccount.name, fakeAccount.email, "password")
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

        coEvery {
            sessionManager.refreshAuthenticationToken(FAKE_TOKEN)
        } returns Unit

        //Act
        val result = useCase(fakeAccount.name, fakeAccount.email, "password")
        val expected = NetworkResult.Success(fakeAccount)
        //Assert
        Truth.assertThat(result).isEqualTo(expected)
    }
}