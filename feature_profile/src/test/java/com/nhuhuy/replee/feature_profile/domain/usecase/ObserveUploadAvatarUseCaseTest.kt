package com.nhuhuy.replee.feature_profile.domain.usecase

import androidx.work.WorkInfo
import com.google.common.truth.Truth
import com.nhuhuy.replee.feature_profile.FakeParameters.Companion.fakeUuid
import com.nhuhuy.replee.feature_profile.data.worker.ProfileScheduler
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ObserveUploadAvatarUseCaseTest {
    private lateinit var profileScheduler: ProfileScheduler
    private lateinit var useCase: ObserveUploadAvatarUseCase

    @Before
    fun setUp() {
        profileScheduler = mockk(relaxed = true)
        useCase = ObserveUploadAvatarUseCase(profileScheduler)
    }

    @Test
    fun `Should return flow of WorkInfo when observing upload avatar`() = runTest {
        // Arrange
        val mockWorkInfo = mockk<WorkInfo>()
        val expectedFlow = flowOf(mockWorkInfo)
        every { profileScheduler.observeUploadAvatarWorker(fakeUuid) } returns expectedFlow

        // Act
        val result = useCase(fakeUuid)

        // Assert
        Truth.assertThat(result).isEqualTo(expectedFlow)
    }
}
