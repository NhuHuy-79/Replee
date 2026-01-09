package com.nhuhuy.replee.feature_auth.repository

import com.google.common.truth.Truth
import com.nhuhuy.replee.core.common.data.model.toAccountEntity
import com.nhuhuy.replee.core.firebase.utils.FirestoreDataNotFoundException
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.database.entity.account.AccountEntity
import com.nhuhuy.replee.core.firebase.AccountDTO
import com.nhuhuy.replee.core.firebase.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.firebase.data_source.FirebaseAuthService
import com.nhuhuy.replee.feature_auth.data.repository.AuthRepositoryImp
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
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
    private lateinit var firebaseAuthService: FirebaseAuthService
    private lateinit var accountLocalDataSource: AccountLocalDataSource

    @Before
    fun setUp() {
        firebaseAuthService = mockk()
        accountNetworkDataSource = mockk()
        accountLocalDataSource = mockk()
        authRepository = AuthRepositoryImp(accountNetworkDataSource,firebaseAuthService, dispatcher = Dispatchers.IO, accountLocalDataSource)
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
            firebaseAuthService.loginWithEmail("email", "password")
        } returns Unit
        coEvery {
            accountNetworkDataSource.getAccountById("id")
        } returns fakeAccount
        coEvery {
            val account = fakeAccount.toAccountEntity()
            accountLocalDataSource.saveAccount(account)
        } returns Unit
        val expected : Resource<String, RemoteFailure> = Resource.Success("id")
        val actual = authRepository.loginWithEmail("email", "password")

        assert(expected == actual)
    }

    @Test
    fun loginWithEmail_whenThrowException_shouldReturnRemoteFailure() = runTest {
        fakeCurrentUserId()
        val exception = Exception("error")
        val expected: Resource<String, RemoteFailure> = Resource.Error(RemoteFailure.Unknown)
        coEvery {
            firebaseAuthService.loginWithEmail("email", "password")
        } throws exception
        val actual = authRepository.loginWithEmail("email", "password")

        Truth.assertThat(actual).isInstanceOf(expected::class.java)
    }

    @Test
    fun signUpWithEmail_shouldReturnSuccessWithId() = runTest {
        fakeCurrentUserId()
        coEvery {
            firebaseAuthService.signUpWithEmail("email", "password")
        } returns Unit
        coEvery {
            accountNetworkDataSource.addAccount(fakeAccount)
        } returns Unit

        coEvery {
            accountLocalDataSource.saveAccount(fakeAccount.toAccountEntity())
        } returns Unit

        val expected : Resource<String, RemoteFailure> = Resource.Success("id")
        val actual = authRepository.signUpWithEmail("name","email", "password")

        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun signUpWithEmail_whenThrowException_shouldReturnRemoteFailure() = runTest {
        fakeCurrentUserId()
        coEvery {
            firebaseAuthService.signUpWithEmail("email", "password")
        } returns Unit
        coEvery {
            accountNetworkDataSource.addAccount(fakeAccount)
        } throws FirestoreDataNotFoundException()

        val expected: Resource<String, RemoteFailure> = Resource.Error(RemoteFailure.Unknown)
        val actual = authRepository.signUpWithEmail("name","email", "password")

        Truth.assertThat(expected).isInstanceOf(actual::class.java)
        coVerify {
            firebaseAuthService.deleteCurrentUser()
        }
    }


    @Test
    fun sendRecoverPasswordEmail_shouldReturnSuccess() = runTest {
        coEvery {
            firebaseAuthService.sendRecoverPasswordEmail("email")
        } returns Unit

        val expected : Resource<Unit, RemoteFailure> = Resource.Success(Unit)
        val actual = authRepository.sendRecoverPasswordEmail("email")

        Truth.assertThat(actual).isEqualTo(expected)
    }


    fun fakeCurrentUserId(){
        coEvery {
            firebaseAuthService.provideCurrentUser().uid
        } returns "id"
    }
}