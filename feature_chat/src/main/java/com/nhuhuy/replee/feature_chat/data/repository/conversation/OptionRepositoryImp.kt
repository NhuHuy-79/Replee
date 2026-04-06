package com.nhuhuy.replee.feature_chat.data.repository.conversation

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.data.data_store.AppDataStore
import com.nhuhuy.replee.core.data.data_store.SeedColor
import com.nhuhuy.replee.core.data.utils.executeWithTimeout
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationNetworkDataSource
import com.nhuhuy.replee.feature_chat.domain.repository.OptionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class OptionRepositoryImp @Inject constructor(
    private val appDataStore: AppDataStore,
    private val ioDispatcher: CoroutineDispatcher,
    private val conversationNetworkDataSource: ConversationNetworkDataSource,
    private val conversationLocalDataSource: ConversationLocalDataSource
) : OptionRepository {

    override suspend fun updateOtherUserNickname(
        uid: String,
        conversationId: String,
        nickName: String
    ): NetworkResult<Unit> {
        return executeWithTimeout(ioDispatcher) {
            conversationLocalDataSource.updateOwnerNickName(conversationId, nickName)
            val conversationDTO =
                conversationNetworkDataSource.fetchConversationById(conversationId)
            conversationDTO?.let {
                conversationNetworkDataSource.updateNicknameForUser(
                    uid, nickName, conversationDTO
                )
            }
            Timber.d("Change Nick Name2")
        }
    }

    override suspend fun muteOtherUser(
        conversationId: String,
        otherUser: String,
        muted: Boolean
    ): NetworkResult<Unit> {
        return executeWithTimeout(ioDispatcher) {
            conversationLocalDataSource.updateMutedStatus(conversationId, muted)
            conversationNetworkDataSource.updateMutedStatus(
                conversationId = conversationId,
                uid = otherUser,
                muted = muted
            )
        }
    }

    override suspend fun pinConversation(
        conversationId: String,
        currentUser: String,
        pinned: Boolean
    ): NetworkResult<Unit> {
        return executeWithTimeout(ioDispatcher) {
            conversationLocalDataSource.updatePinnedStatus(conversationId, pinned)
            conversationNetworkDataSource.updatePinnedStatus(
                conversationId = conversationId,
                uid = currentUser,
                pinned = pinned
            )
        }
    }

    override suspend fun deleteConversation(conversationId: String): NetworkResult<Unit> {
        return executeWithTimeout {

        }
    }

    override fun observeChatColor(): Flow<SeedColor> {
        return appDataStore.observeChatColor()
    }

    override suspend fun selectColor(color: SeedColor) {
        return withContext(ioDispatcher) {
            appDataStore.saveChatColor(color)
        }
    }
}