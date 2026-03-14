package com.nhuhuy.replee.core.common.data.repository

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.common.data.data_source.SessionDataSource
import com.nhuhuy.replee.core.common.utils.execute
import javax.inject.Inject

interface SessionManager {
    suspend fun getAuthenticationId(): NetworkResult<String>
}

class SessionManagerImp @Inject constructor(
    private val sessionNetworkDataSource: SessionDataSource
) : SessionManager {
    override suspend fun getAuthenticationId(): NetworkResult<String> {
        return execute {
            sessionNetworkDataSource.provideAuthenticationId()
        }
    }

}