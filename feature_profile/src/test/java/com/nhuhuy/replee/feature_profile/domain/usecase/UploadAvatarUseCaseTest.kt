package com.nhuhuy.replee.feature_profile.domain.usecase

import com.google.common.truth.Truth
import com.nhuhuy.core.domain.repository.FileMetadata
import com.nhuhuy.core.domain.repository.FileRepository
import com.nhuhuy.replee.feature_profile.FakeParameters.Companion.fakeAccount
import com.nhuhuy.replee.feature_profile.FakeParameters.Companion.fakeInternalPath
import com.nhuhuy.replee.feature_profile.FakeParameters.Companion.fakeUriPath
import com.nhuhuy.replee.feature_profile.FakeParameters.Companion.fakeUuid
import com.nhuhuy.replee.feature_profile.data.worker.ProfileScheduler
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UploadAvatarUseCaseTest {
    private lateinit var fileRepository: FileRepository
    private lateinit var profileScheduler: ProfileScheduler
    private lateinit var useCase: UploadAvatarUseCase

    @Before
    fun setUp() {
        fileRepository = mockk(relaxed = true)
        profileScheduler = mockk(relaxed = true)
        useCase = UploadAvatarUseCase(fileRepository, profileScheduler)
    }

    @Test
    fun `Should return UUID and call internal methods when avatar upload process starts successfully`() =
        runTest {
            // Arrange
            coEvery { fileRepository.saveFileToInternalStorage(fakeUriPath) } returns fakeInternalPath
            coEvery { fileRepository.getFileMetadata(fakeUriPath) } returns FileMetadata(
                width = 100, height = 100, mimeType = "image/jpeg", size = 1024L
            )
            coEvery { profileScheduler.schedulerUploadAvatar(fakeAccount.id) } returns fakeUuid

            // Act
            val result = useCase(fakeAccount.id, fakeUriPath)

            // Assert
            Truth.assertThat(result).isEqualTo(fakeUuid)
            coVerify {
                fileRepository.saveFileToInternalStorage(fakeUriPath)
                fileRepository.getFileMetadata(fakeUriPath)
                fileRepository.upsertFilePath(any())
                profileScheduler.schedulerUploadAvatar(fakeAccount.id)
            }
        }
}
