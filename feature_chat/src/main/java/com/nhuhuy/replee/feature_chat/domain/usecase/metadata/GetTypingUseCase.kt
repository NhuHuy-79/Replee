package com.nhuhuy.replee.feature_chat.domain.usecase.metadata

import com.nhuhuy.replee.feature_chat.domain.repository.MetaDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTypingUseCase @Inject constructor(
    private val metaDataRepository: MetaDataRepository
) {
    operator fun invoke(conversationId: String): Flow<List<String>> {
        return metaDataRepository.getOtherTyping(conversationId)
    }
}