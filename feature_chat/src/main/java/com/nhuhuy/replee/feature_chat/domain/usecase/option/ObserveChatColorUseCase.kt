package com.nhuhuy.replee.feature_chat.domain.usecase.option

import com.nhuhuy.replee.core.data.data_store.SeedColor
import com.nhuhuy.replee.feature_chat.domain.repository.OptionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveChatColorUseCase @Inject constructor(
    private val optionRepository: OptionRepository
) {
    operator fun invoke(): Flow<SeedColor> {
        return optionRepository.observeChatColor()
    }
}