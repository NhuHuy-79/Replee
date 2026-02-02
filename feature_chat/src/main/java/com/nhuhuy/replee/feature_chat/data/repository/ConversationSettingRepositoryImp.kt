package com.nhuhuy.replee.feature_chat.data.repository

import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.error_handling.safeCall
import com.nhuhuy.replee.core.common.toRemoteFailure
import com.nhuhuy.replee.feature_chat.data.data_store.SeedColor
import com.nhuhuy.replee.feature_chat.data.model.network.ConversationDTO
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationNetworkDataSource
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationSettingRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class ConversationSettingRepositoryImp @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val conversationNetworkDataSource: ConversationNetworkDataSource,
    private val conversationLocalDataSource: ConversationLocalDataSource
) : ConversationSettingRepository {
    override suspend fun updateSeedColor(seedColor: SeedColor) {
        return withContext(dispatcher) {
            TODO("Update seed color")
        }
    }

    override suspend fun updateOwnerNickname(
        uid: String,
        conversationId: String,
        nickName: String
    ): Resource<Unit, RemoteFailure> {
        return withContext(dispatcher) {
            safeCall(
                throwable = { e ->
                    e.toRemoteFailure()
                }
            ) {
                conversationLocalDataSource.updateOwnerNickName(conversationId, nickName)
                val conversationDTO =
                    conversationNetworkDataSource.fetchConversationById(conversationId)
                conversationDTO?.let {
                    conversationNetworkDataSource.updateNicknameForUser(
                        uid,
                        conversationId,
                        conversationDTO
                    )
                }
                Timber.d("Change Nick Name1")
            }
        }
    }

    override suspend fun updateOtherUserNickname(
        uid: String,
        conversationId: String,
        nickName: String
    ): Resource<Unit, RemoteFailure> {
        return withContext(dispatcher) {
            safeCall(
                throwable = { e ->
                    Timber.e(e)
                    e.toRemoteFailure()
                }
            ) {
                /*conversationLocalDataSource.updateOwnerNickName(conversationId, nickName)*/
                /*val conversationDTO = conversationNetworkDataSource.fetchConversationById(conversationId)*/
                conversationNetworkDataSource.updateNicknameForUser(
                    uid, nickName,
                    ConversationDTO()
                )
                Timber.d("Change Nick Name2")
            }
        }
    }

    override suspend fun muteOtherUser(
        conversationId: String,
        otherUser: String,
        muted: Boolean
    ): Resource<Unit, RemoteFailure> {
        return withContext(dispatcher) {
            conversationLocalDataSource.updateMutedStatus(conversationId, muted)
            safeCall(
                throwable = { e ->
                    Timber.e(e)
                    e.toRemoteFailure()
                }
            ) {
                conversationNetworkDataSource.updateMutedStatus(conversationId, otherUser, muted)
            }
        }
    }

    override suspend fun pinConversation(
        conversationId: String,
        currentUser: String,
        pinned: Boolean
    ): Resource<Unit, RemoteFailure> {
        return withContext(dispatcher) {
            conversationLocalDataSource.updatePinnedStatus(conversationId, pinned)
            safeCall(
                throwable = { e -> e.toRemoteFailure() }
            ) {
                conversationNetworkDataSource.updatePinnedStatus(
                    conversationId,
                    currentUser,
                    pinned
                )
            }
        }
    }

    override suspend fun blockOtherUser(
        conversationId: String,
        otherUser: String,
        blocked: Boolean
    ): Resource<Unit, RemoteFailure> {
        return withContext(dispatcher) {
            conversationLocalDataSource.updateBlockStatus(conversationId, blocked)
            safeCall(
                throwable = { e -> e.toRemoteFailure() }
            ) {
                //
            }
        }
    }

    override suspend fun deleteConversation(conversationId: String): Resource<Unit, RemoteFailure> {
        return withContext(dispatcher) {
            //Delete conversation
            safeCall(
                throwable = { e -> e.toRemoteFailure() }
            ) {
                //TODO("delete conversation")
            }
        }
    }

}