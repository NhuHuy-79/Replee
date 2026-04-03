package com.nhuhuy.replee.feature_profile.domain.usecase

import com.google.common.truth.Truth
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.repository.AccountRepository
import com.nhuhuy.replee.feature_profile.FakeParameters.Companion.fakeAccount
import com.nhuhuy.replee.feature_profile.FakeParameters.Companion.fakeException
import com.nhuhuy.replee.feature_profile.domain.repository.ProfileRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UpdatePasswordUseCaseTest {
    private lateinit var accountRepository: AccountRepository
    private lateinit var profileRepository: ProfileRepository
    private lateinit var useCase: UpdatePasswordUseCase

    @Before
    fun setUp() {
        accountRepository = mockk(relaxed = true)
        profileRepository = mockk(relaxed = true)
        useCase = UpdatePasswordUseCase(accountRepository, profileRepository)
    }

    @Test
    fun `Should return success when password update is successful`() = runTest {
        // Arrange
        coEvery { accountRepository.getCurrentAccount() } returns fakeAccount
        coEvery {
            profileRepository.updatePassword(any(), any(), any())
        } returns NetworkResult.Success(Unit)

        // Act
        val result = useCase("oldPassword", "newPassword")

        // Assert
        Truth.assertThat(result).isEqualTo(NetworkResult.Success(Unit))
    }

    @Test
    fun `Should return failure when password update fails`() = runTest {
        // Arrange
        coEvery { accountRepository.getCurrentAccount() } returns fakeAccount
        coEvery {
            profileRepository.updatePassword(any(), any(), any())
        } returns NetworkResult.Failure(fakeException)

        // Act
        val result = useCase("oldPassword", "newPassword")

        // Assert
        Truth.assertThat(result).isEqualTo(NetworkResult.Failure(fakeException))
    }
}
