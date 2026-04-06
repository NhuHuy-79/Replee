package com.nhuhuy.replee.feature_chat.domain.usecase.metadata

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.feature_chat.domain.repository.MetaDataRepository
import javax.inject.Inject

class UpdateTypingUseCase @Inject constructor(
    private val metaDataRepository: MetaDataRepository
) {
    suspend operator fun invoke(
        conversationId: String,
        userId: String,
        typing: Boolean
    ): NetworkResult<Unit> {
        return metaDataRepository.updateMyTyping(
            conversationId = conversationId,
            userId = userId,
            typing = typing
        )
    }
}