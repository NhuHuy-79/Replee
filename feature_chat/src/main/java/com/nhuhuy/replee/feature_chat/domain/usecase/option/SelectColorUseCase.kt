package com.nhuhuy.replee.feature_chat.domain.usecase.option

import com.nhuhuy.replee.core.model.SeedColor
import com.nhuhuy.replee.core.domain.repository.OptionRepository
import javax.inject.Inject

class SelectColorUseCase @Inject constructor(
    private val optionRepository: OptionRepository
) {
    suspend operator fun invoke(color: SeedColor) {
        optionRepository.selectColor(color)
    }
}
