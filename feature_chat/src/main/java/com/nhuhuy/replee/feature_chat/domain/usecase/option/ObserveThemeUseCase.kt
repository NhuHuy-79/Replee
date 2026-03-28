package com.nhuhuy.replee.feature_chat.domain.usecase.option

import com.nhuhuy.replee.core.data.data_store.AppDataStore
import javax.inject.Inject

class ObserveThemeUseCase @Inject constructor(
    private val dataStore: AppDataStore
) {
    operator fun invoke() = dataStore.observeTheme()
}