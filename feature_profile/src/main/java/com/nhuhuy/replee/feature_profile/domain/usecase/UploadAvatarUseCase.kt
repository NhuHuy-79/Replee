package com.nhuhuy.replee.feature_profile.domain.usecase

import com.nhuhuy.replee.core.model.FilePath
import com.nhuhuy.replee.core.domain.repository.FileMetadata
import com.nhuhuy.replee.core.domain.repository.FileRepository
import com.nhuhuy.replee.core.domain.worker.WorkerScheduler
import java.util.UUID
import javax.inject.Inject

class UploadAvatarUseCase @Inject constructor(
    private val fileRepository: FileRepository,
    private val workerScheduler: WorkerScheduler
) {
    suspend operator fun invoke(uid: String, uriPath: String): String {
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

        workerScheduler.scheduleUploadAvatar(uid = uid)
        
        return uid
    }
}
