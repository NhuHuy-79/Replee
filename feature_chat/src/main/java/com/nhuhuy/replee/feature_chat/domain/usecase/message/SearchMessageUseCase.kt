package com.nhuhuy.replee.feature_chat.domain.usecase.message

import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import javax.inject.Inject

class SearchMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository
)