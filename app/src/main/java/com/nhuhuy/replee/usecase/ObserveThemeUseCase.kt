package com.nhuhuy.replee.usecase

import com.nhuhuy.replee.core.common.data.data_store.AppDataStore
import javax.inject.Inject

class ObserveThemeUseCase @Inject constructor(
    private val dataStore: AppDataStore
) {
    operator fun invoke() = dataStore.observeTheme()
}