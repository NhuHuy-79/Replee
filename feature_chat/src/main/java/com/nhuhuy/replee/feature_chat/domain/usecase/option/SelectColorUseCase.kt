package com.nhuhuy.replee.feature_chat.domain.usecase.option

import com.nhuhuy.replee.core.data.data_store.SeedColor
import com.nhuhuy.replee.feature_chat.domain.repository.OptionRepository
import javax.inject.Inject

class SelectColorUseCase @Inject constructor(
    private val optionRepository: OptionRepository
) {
    suspend operator fun invoke(color: SeedColor) {
        optionRepository.selectColor(color)
    }
}