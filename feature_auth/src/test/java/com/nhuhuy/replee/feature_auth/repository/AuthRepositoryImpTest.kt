package com.nhuhuy.replee.feature_auth.repository

import com.google.common.truth.Truth
import com.nhuhuy.replee.core.common.data.model.toAccountEntity
import com.nhuhuy.replee.core.common.data.preferences.AppPreferences
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.firebase.data.AccountDTO
import com.nhuhuy.replee.core.firebase.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.firebase.data_source.FirebaseAuthEmailService
import com.nhuhuy.replee.core.firebase.utils.FirestoreDataNotFoundException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AuthRepositoryImpTest {
    private lateinit var authRepository: AuthRepository
    private lateinit var accountNetworkDataSource: AccountNetworkDataSource
    private lateinit var firebaseAuthEmailService: FirebaseAuthEmailService
    private lateinit var appPreferences: AppPreferences
    private lateinit var accountLocalDataSource: AccountLocalDataSource

    @Before
    fun setUp() {
        firebaseAuthEmailService = mockk()
        accountNetworkDataSource = mockk()
        accountLocalDataSource = mockk()
        appPreferences = mockk()
        authRepository = AuthRepositoryImp(
            accountNetworkDataSource,
            firebaseAuthEmailService,
            dispatcher = Dispatchers.IO,
            accountLocalDataSource,
            appPreferences
        )
    }

    private val fakeAccount = AccountDTO(
        id = "id",
        name = "name",
        email = "email",
    )

    @Test
    fun loginWithEmail_shouldReturnSuccessWithId() = runTest {
        fakeCurrentUserId()
        coEvery {
            firebaseAuthEmailService.loginWithEmail("email", "password")
        } returns Unit

        coEvery {
            accountNetworkDataSource.fetchAccountById("id")
        } returns fakeAccount

        coEvery {
            val account = fakeAccount.toAccountEntity()
            accountLocalDataSource.upsertAccount(account.copy(logOut = false))
        } returns Unit

        coEvery {
            firebaseAuthEmailService.getDeviceToken()
        } returns "token"

        coEvery {
            accountNetworkDataSource.updateDeviceToken("id", "token")
        } returns Unit

        coEvery {
            appPreferences.saveLoggedStatus(true)
        } returns Unit

        val expected : Resource<String, RemoteFailure> = Resource.Success("id")
        val actual = authRepository.loginWithEmail("email", "password")

        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun loginWithEmail_whenThrowException_shouldReturnRemoteFailure() = runTest {
        fakeCurrentUserId()
        val exception = Exception("error")
        val expected: Resource<String, RemoteFailure> = Resource.Error(RemoteFailure.Unknown)
        coEvery {
            firebaseAuthEmailService.loginWithEmail("email", "password")
        } throws exception
        val actual = authRepository.loginWithEmail("email", "password")

        Truth.assertThat(actual).isInstanceOf(expected::class.java)
    }

    @Test
    fun signUpWithEmail_shouldReturnSuccessWithId() = runTest {
        fakeCurrentUserId()
        coEvery {
            firebaseAuthEmailService.signUpWithEmail("email", "password")
        } returns Unit
        coEvery {
            accountNetworkDataSource.sendAccount(fakeAccount)
        } returns Unit

        coEvery {
            val account = fakeAccount.toAccountEntity()
            accountLocalDataSource.upsertAccount(account.copy(logOut = false))
        } returns Unit

        coEvery {
            firebaseAuthEmailService.getDeviceToken()
        } returns "token"

        coEvery {
            accountNetworkDataSource.updateDeviceToken("id", "token")
        } returns Unit

        coEvery {
            appPreferences.saveLoggedStatus(true)
        } returns Unit

        val expected : Resource<String, RemoteFailure> = Resource.Success("id")
        val actual = authRepository.signUpWithEmail("name","email", "password")

        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun signUpWithEmail_whenThrowException_shouldReturnRemoteFailure() = runTest {
        fakeCurrentUserId()
        coEvery {
            firebaseAuthEmailService.signUpWithEmail("email", "password")
        } returns Unit
        coEvery {
            accountNetworkDataSource.sendAccount(fakeAccount)
        } throws FirestoreDataNotFoundException()

        val expected: Resource<String, RemoteFailure> = Resource.Error(RemoteFailure.Unknown)
        val actual = authRepository.signUpWithEmail("name","email", "password")

        Truth.assertThat(expected).isInstanceOf(actual::class.java)
        coVerify {
            firebaseAuthEmailService.deleteCurrentUser()
        }
    }


    @Test
    fun sendRecoverPasswordEmail_shouldReturnSuccess() = runTest {
        coEvery {
            firebaseAuthEmailService.sendRecoverPasswordEmail("email")
        } returns Unit

        val expected : Resource<Unit, RemoteFailure> = Resource.Success(Unit)
        val actual = authRepository.sendRecoverPasswordEmail("email")

        Truth.assertThat(actual).isEqualTo(expected)
    }


    fun fakeCurrentUserId(){
        coEvery {
            firebaseAuthEmailService.getCurrentUser().uid
        } returns "id"
    }
}