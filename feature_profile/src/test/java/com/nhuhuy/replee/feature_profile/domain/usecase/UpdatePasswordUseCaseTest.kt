package com.nhuhuy.replee.feature_profile.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.nhuhuy.replee.core.domain.repository.AccountRepository
import com.nhuhuy.replee.core.domain.repository.ProfileRepository
import com.nhuhuy.replee.core.model.account.Account
import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UpdatePasswordUseCaseTest {

    private lateinit var updatePasswordUseCase: UpdatePasswordUseCase
    private val accountRepository = mockk<AccountRepository>()
    private val profileRepository = mockk<ProfileRepository>()

    @Before
    fun setUp() {
        updatePasswordUseCase = UpdatePasswordUseCase(accountRepository, profileRepository)
    }

    @Test
    fun `When update password is called, it should return success from repository`() = runTest {
        // Arrange
        val email = "test@example.com"
        val oldPass = "old123"
        val newPass = "new123"
        coEvery { accountRepository.getCurrentAccount() } returns Account(email = email)
        coEvery {
            profileRepository.updatePassword(email, oldPass, newPass) 
        } returns NetworkResult.Success(Unit)

        // Act
        val result = updatePasswordUseCase(oldPass, newPass)

        // Assert
        assertThat(result).isInstanceOf(NetworkResult.Success::class.java)
    }

    @Test
    fun `When update password fails, it should return failure`() = runTest {
        // Arrange
        val email = "test@example.com"
        val exception = Exception("Network Error")
        coEvery { accountRepository.getCurrentAccount() } returns Account(email = email)
        coEvery {
            profileRepository.updatePassword(any(), any(), any())
        } returns NetworkResult.Failure(exception)

        // Act
        val result = updatePasswordUseCase("old", "new")

        // Assert
        assertThat(result).isInstanceOf(NetworkResult.Failure::class.java)
        val failure = result as NetworkResult.Failure
        assertThat(failure.throwable).isEqualTo(exception)
    }
}
