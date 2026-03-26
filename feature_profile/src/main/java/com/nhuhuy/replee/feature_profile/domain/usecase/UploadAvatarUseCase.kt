package com.nhuhuy.replee.feature_profile.domain.usecase

import com.nhuhuy.core.domain.model.FilePath
import com.nhuhuy.core.domain.repository.FileMetadata
import com.nhuhuy.core.domain.repository.FileRepository
import com.nhuhuy.replee.feature_profile.data.worker.ProfileScheduler
import javax.inject.Inject

class UploadAvatarUseCase @Inject constructor(
    private val fileRepository: FileRepository,
    private val profileScheduler: ProfileScheduler
) {
    suspend operator fun invoke(uid: String, uriPath: String) {
        val tempFilePath: String = fileRepository.saveFileToInternalStorage(uriPath)
        val metadata: FileMetadata = fileRepository.getFileMetadata(uriPath)

        val localPathFile = FilePath(
            userId = uid,
            localPath = tempFilePath,
            width = metadata.width,
            height = metadata.height,
            fileType = metadata.mimeType,
            fileSize = metadata.size,
        )

        fileRepository.upsertFilePath(localPathFile)

        profileScheduler.schedulerUploadAvatar(uid = uid)

    }
}