package com.nhuhuy.replee.core.network.data_source

import com.nhuhuy.replee.core.network.model.AccountDTO

interface GoogleAuthService {
    suspend fun signIn(idToken: String): AccountDTO
}

