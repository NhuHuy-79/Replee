package com.nhuhuy.replee.feature_auth.repository

import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.network.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.FirebaseAuthEmailService
import com.nhuhuy.replee.core.network.model.AccountDTO
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AuthRepositoryImpTest {
    private lateinit var accountNetworkDataSource: AccountNetworkDataSource
    private lateinit var firebaseAuthEmailService: FirebaseAuthEmailService
    private lateinit var accountLocalDataSource: AccountLocalDataSource

    @Before
    fun setUp() {
        firebaseAuthEmailService = mockk()
        accountNetworkDataSource = mockk()
        accountLocalDataSource = mockk()
        appPreferences = mockk()

    }

    private val fakeAccount = AccountDTO(
        id = "id",
        name = "name",
        email = "email",
    )

    @Test
    fun loginWithEmail_shouldReturnSuccessWithId() = runTest {
        fakeCurrentUserId()

    }

    @Test
    fun loginWithEmail_whenThrowException_shouldReturnRemoteFailure() = runTest {

    }

    @Test
    fun signUpWithEmail_shouldReturnSuccessWithId() = runTest {

    }

    @Test
    fun signUpWithEmail_whenThrowException_shouldReturnRemoteFailure() = runTest {
        fakeCurrentUserId()

    }


    @Test
    fun sendRecoverPasswordEmail_shouldReturnSuccess() = runTest {

    }


    fun fakeCurrentUserId(){
        coEvery {
            firebaseAuthEmailService.getCurrentUser().uid
        } returns "id"
    }
}