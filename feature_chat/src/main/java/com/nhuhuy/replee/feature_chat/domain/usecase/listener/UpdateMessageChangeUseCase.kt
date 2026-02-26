package com.nhuhuy.replee.feature_chat.domain.usecase.listener

import com.nhuhuy.replee.core.network.model.DataChange
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import timber.log.Timber
import javax.inject.Inject

class UpdateMessageChangeUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(
        dataChanges: List<DataChange<Message>>
    ) {
        val upserts: MutableList<Message> = mutableListOf()
        val deletes: MutableList<String> = mutableListOf()

        for (change in dataChanges) {
            when (change) {
                is DataChange.Delete -> deletes.add(change.id)
                is DataChange.Upsert -> upserts.add(change.data)
            }
        }

        Timber.d("Upserts: $upserts")
        Timber.d("Deletes: $deletes")

        messageRepository.updateLocalDataChange(
            upsert = upserts,
            delete = deletes
        )
    }
}