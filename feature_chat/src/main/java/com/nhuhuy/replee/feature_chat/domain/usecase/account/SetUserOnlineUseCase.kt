package com.nhuhuy.replee.feature_chat.domain.usecase.account

import com.nhuhuy.core.domain.repository.PresenceRepository
import javax.inject.Inject

class SetUserOnlineUseCase @Inject constructor(
    private val presenceRepository: PresenceRepository
) {
    suspend operator fun invoke(uid: String) {
        presenceRepository.setOnline(uid)
    }
}