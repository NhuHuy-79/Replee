package com.nhuhuy.replee.feature_chat.data.repository.conversation

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.data.utils.execute
import com.nhuhuy.replee.core.data.utils.executeWithTimeout
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationNetworkDataSource
import com.nhuhuy.replee.feature_chat.domain.repository.OptionRepository
import kotlinx.coroutines.CoroutineDispatcher
import timber.log.Timber
import javax.inject.Inject

class OptionRepositoryImp @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val conversationNetworkDataSource: ConversationNetworkDataSource,
    private val conversationLocalDataSource: ConversationLocalDataSource
) : OptionRepository {

    override suspend fun updateOtherUserNickname(
        uid: String,
        conversationId: String,
        nickName: String
    ): NetworkResult<Unit> {
        return executeWithTimeout {
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
        return execute {
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
        return execute {
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
}