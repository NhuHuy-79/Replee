package com.nhuhuy.replee.core.data.repository

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.repository.PresenceRepository
import com.nhuhuy.replee.core.data.utils.executeWithTimeout
import com.nhuhuy.replee.core.network.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.PresenceNetworkDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PresenceRepositoryImp @Inject constructor(
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val presenceNetworkDataSource: PresenceNetworkDataSource
) : PresenceRepository {
    override suspend fun setOnline(uid: String): NetworkResult<String> {
        return executeWithTimeout {
            presenceNetworkDataSource.setOnline(uid)
            accountNetworkDataSource.updateOnlineStatus(uid = uid, online = true)
            uid
        }
    }

    override suspend fun setOffline(uid: String): NetworkResult<String> {
        return executeWithTimeout {
            presenceNetworkDataSource.setOffline(uid)
            accountNetworkDataSource.updateOnlineStatus(uid = uid, online = false)
            uid
        }
    }

    override fun observeOnlineState(uid: String): Flow<Boolean> {
        return presenceNetworkDataSource.observeUserStatus(uid)
    }
}