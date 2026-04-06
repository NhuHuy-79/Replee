package com.nhuhuy.replee.feature_chat.domain.usecase.metadata

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.feature_chat.domain.repository.MetaDataRepository
import javax.inject.Inject

class UpdateReadTimeUseCase @Inject constructor(
    private val metaDataRepository: MetaDataRepository
) {
    suspend operator fun invoke(
        conversationId: String,
        userId: String,
        currentTime: Long,
    ): NetworkResult<Unit> {
        return metaDataRepository.updateMyReading(
            conversationId = conversationId,
            userId = userId,
            reading = currentTime
        )
    }
}