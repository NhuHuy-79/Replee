package com.nhuhuy.replee.feature_chat.domain.usecase.file

import com.nhuhuy.core.domain.model.ValidateFileResult
import com.nhuhuy.core.domain.repository.FileRepository
import javax.inject.Inject

class ValidateFileSizeUseCase @Inject constructor(
    private val fileRepository: FileRepository
) {
    suspend operator fun invoke(uriPath: String): ValidateFileResult {
        return fileRepository.validateFileSize(uriPath)
    }
}