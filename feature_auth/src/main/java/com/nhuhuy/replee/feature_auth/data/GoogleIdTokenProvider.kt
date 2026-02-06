package com.nhuhuy.replee.feature_auth.data

import com.nhuhuy.core.domain.model.NetworkResult

interface GoogleIdTokenProvider {
    suspend fun getIdToken(): NetworkResult<String>
}