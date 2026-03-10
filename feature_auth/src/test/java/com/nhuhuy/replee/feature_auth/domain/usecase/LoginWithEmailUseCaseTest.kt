package com.nhuhuy.replee.feature_auth.domain.usecase

import com.google.common.truth.Truth
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class LoginWithEmailUseCaseTest {
    private lateinit var authRepository: AuthRepository
    private lateinit var useCase: LoginWithEmailUseCase

    @Before
    fun setUp() {
        authRepository = mockk(relaxed = true)
        useCase = LoginWithEmailUseCase(authRepository)
    }

    @Test
    fun `should return success when login with email and password`() = runTest {
        //Arrange
        coEvery {
            authRepository.loginWithEmail(
                "email",
                "password"
            )
        } returns NetworkResult.Success("uid")

        //Act
        val result = useCase.invoke("email", "password")

        //Assert
        Truth.assertThat(result).isEqualTo(NetworkResult.Success("uid"))

        coVerify { authRepository.loginWithEmail("email", "password") }
    }

    @Test
    fun `given repository failure when login then return failure`() = runTest {
        // Arrange
        val error = Exception("network error")

        coEvery {
            authRepository.loginWithEmail(any(), any())
        } returns NetworkResult.Failure(error)

        // Act
        val result = useCase("email", "password")

        // Assert
        Truth.assertThat(result).isInstanceOf(NetworkResult.Failure::class.java)

        coVerify { authRepository.loginWithEmail("email", "password") }
    }

}