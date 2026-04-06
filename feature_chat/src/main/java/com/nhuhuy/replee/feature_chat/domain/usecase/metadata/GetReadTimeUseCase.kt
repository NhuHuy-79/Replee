package com.nhuhuy.replee.feature_chat.domain.usecase.metadata

import com.nhuhuy.replee.feature_chat.domain.repository.MetaDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReadTimeUseCase @Inject constructor(
    private val metadataRepository: MetaDataRepository
) {
    operator fun invoke(conversationId: String, otherUserId: String): Flow<Long> {
        return metadataRepository.getOtherReading(
            conversationId = conversationId,
            otherUserId = otherUserId
        )
    }
}