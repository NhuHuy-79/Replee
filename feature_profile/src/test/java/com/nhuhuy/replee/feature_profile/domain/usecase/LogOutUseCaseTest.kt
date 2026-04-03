package com.nhuhuy.replee.feature_profile.domain.usecase

import com.nhuhuy.core.domain.SessionManager
import com.nhuhuy.core.domain.repository.PresenceRepository
import com.nhuhuy.replee.feature_profile.FakeParameters.Companion.fakeAccount
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class LogOutUseCaseTest {
    private lateinit var sessionManager: SessionManager
    private lateinit var presenceRepository: PresenceRepository
    private lateinit var useCase: LogOutUseCase

    @Before
    fun setUp() {
        sessionManager = mockk(relaxed = true)
        presenceRepository = mockk(relaxed = true)
        useCase = LogOutUseCase(sessionManager, presenceRepository)
    }

    @Test
    fun `Should set offline and logout when user id is not null`() = runTest {
        // Arrange
        coEvery { sessionManager.getUserIdOrNull() } returns fakeAccount.id

        // Act
        useCase()

        // Assert
        coVerify {
            presenceRepository.setOffline(fakeAccount.id)
            sessionManager.logout()
        }
    }

    @Test
    fun `Should only logout when user id is null`() = runTest {
        // Arrange
        coEvery { sessionManager.getUserIdOrNull() } returns null

        // Act
        useCase()

        // Assert
        coVerify(exactly = 0) { presenceRepository.setOffline(any()) }
        coVerify { sessionManager.logout() }
    }
}
