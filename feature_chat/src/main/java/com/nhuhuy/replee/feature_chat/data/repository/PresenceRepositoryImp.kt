package com.nhuhuy.replee.feature_chat.data.repository

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.common.utils.executeWithTimeout
import com.nhuhuy.replee.feature_chat.data.source.presence.PresenceNetworkDataSource
import com.nhuhuy.replee.feature_chat.domain.repository.PresenceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PresenceRepositoryImp @Inject constructor(
    private val presenceNetworkDataSource: PresenceNetworkDataSource
) : PresenceRepository {
    override suspend fun setOnline(uid: String): NetworkResult<String> {
        return executeWithTimeout {
            presenceNetworkDataSource.setOnline(uid)
            uid
        }
    }

    override suspend fun setOffline(uid: String): NetworkResult<String> {
        return executeWithTimeout {
            presenceNetworkDataSource.setOffline(uid)
            uid
        }
    }

    override fun observeOnlineState(uid: String): Flow<Boolean> {
        return presenceNetworkDataSource.observeUserStatus(uid)
    }

}