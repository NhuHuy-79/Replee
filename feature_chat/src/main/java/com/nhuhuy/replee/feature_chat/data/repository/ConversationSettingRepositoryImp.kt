package com.nhuhuy.replee.feature_chat.data.repository

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.common.utils.ioExecuteWithTimeout
import com.nhuhuy.replee.feature_chat.data.data_store.SeedColor
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationNetworkDataSource
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationSettingRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class ConversationSettingRepositoryImp @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val conversationNetworkDataSource: ConversationNetworkDataSource,
    private val conversationLocalDataSource: ConversationLocalDataSource
) : ConversationSettingRepository {
    override suspend fun updateSeedColor(seedColor: SeedColor) {
        return withContext(ioDispatcher) {
            TODO("Update seed color")
        }
    }

    override suspend fun updateOwnerNickname(
        uid: String,
        conversationId: String,
        nickName: String
    ): NetworkResult<Unit> {
        return ioExecuteWithTimeout {
            conversationLocalDataSource.updateOwnerNickName(conversationId, nickName)
            val conversationDTO =
                conversationNetworkDataSource.fetchConversationById(conversationId)
            conversationDTO?.let {
                conversationNetworkDataSource.updateNicknameForUser(
                    uid,
                    nickName,
                    conversationDTO
                )
            }
            Timber.d("Change Nick Name1")
        }
    }

    override suspend fun updateOtherUserNickname(
        uid: String,
        conversationId: String,
        nickName: String
    ): NetworkResult<Unit> {
        return ioExecuteWithTimeout {
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
        return ioExecuteWithTimeout {
            conversationLocalDataSource.updateMutedStatus(conversationId, muted)
            conversationNetworkDataSource.updateMutedStatus(conversationId, otherUser, muted)
        }
    }

    override suspend fun pinConversation(
        conversationId: String,
        currentUser: String,
        pinned: Boolean
    ): NetworkResult<Unit> {
        return ioExecuteWithTimeout {
            conversationLocalDataSource.updatePinnedStatus(conversationId, pinned)
            conversationNetworkDataSource.updatePinnedStatus(
                conversationId,
                currentUser,
                pinned
            )
        }
    }

    override suspend fun blockOtherUser(
        conversationId: String,
        otherUser: String,
        blocked: Boolean
    ): NetworkResult<Unit> {
        return ioExecuteWithTimeout {
            conversationLocalDataSource.updateBlockStatus(conversationId, blocked)
        }
    }

    override suspend fun deleteConversation(conversationId: String): NetworkResult<Unit> {
        return ioExecuteWithTimeout {

        }
    }
}