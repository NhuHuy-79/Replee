package com.nhuhuy.replee.feature_chat.data.repository

import com.nhuhuy.replee.feature_chat.data.data_store.SeedColor
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationSettingRepository
import javax.inject.Inject

class ConversationSettingRepositoryImp @Inject constructor(

) : ConversationSettingRepository {
    override fun updateSeedColor(seedColor: SeedColor) {
        TODO("Not yet implemented")
    }

    override fun muteOtherUser(otherUserId: String, conversationId: String) {
        TODO("Not yet implemented")
    }

    override fun pinConversation(conversationId: String) {
        TODO("Not yet implemented")
    }

    override fun blockOtherUser(otherUserId: String, conversationId: String) {
        TODO("Not yet implemented")
    }

    override fun deleteConversation(conversationId: String) {
        TODO("Not yet implemented")
    }
}