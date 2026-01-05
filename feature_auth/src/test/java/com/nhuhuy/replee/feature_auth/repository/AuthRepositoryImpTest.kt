package com.nhuhuy.replee.feature_auth.repository

import com.google.common.truth.Truth
import com.nhuhuy.replee.core.firebase.utils.FirestoreDataNotFoundException
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.firebase.AccountDTO
import com.nhuhuy.replee.core.firebase.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.firebase.data_source.AuthDataSource
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
    private lateinit var authDataSource: AuthDataSource

    @Before
    fun setUp() {
        authDataSource = mockk()
        accountNetworkDataSource = mockk()
        authRepository = AuthRepositoryImp(accountNetworkDataSource,authDataSource, dispatcher = Dispatchers.IO)
    }

    @Test
    fun loginWithEmail_shouldReturnSuccessWithId() = runTest {
        fakeCurrentUserId()
        coEvery {
            authDataSource.loginWithEmail("email", "password")
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
            authDataSource.loginWithEmail("email", "password")
        } throws exception
        val actual = authRepository.loginWithEmail("email", "password")

        Truth.assertThat(actual).isInstanceOf(expected::class.java)
    }

    @Test
    fun signUpWithEmail_shouldReturnSuccessWithId() = runTest {
        fakeCurrentUserId()
        coEvery {
            authDataSource.signUpWithEmail("email", "password")
        } returns Unit
        coEvery {
            val account = AccountDTO(
                id = "id",
                name = "name",
                email = "email",
            )
            accountNetworkDataSource.addAccount(account)
        } returns Unit

        val expected : Resource<String, RemoteFailure> = Resource.Success("id")
        val actual = authRepository.signUpWithEmail("name","email", "password")

        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun signUpWithEmail_whenThrowException_shouldReturnRemoteFailure() = runTest {
        fakeCurrentUserId()
        coEvery {
            authDataSource.signUpWithEmail("email", "password")
        } returns Unit
        coEvery {
            val account = AccountDTO(
                id = "id",
                name = "name",
                email = "email",
            )
            accountNetworkDataSource.addAccount(account)
        } throws FirestoreDataNotFoundException()

        val expected: Resource<String, RemoteFailure> = Resource.Error(RemoteFailure.Unknown)
        val actual = authRepository.signUpWithEmail("name","email", "password")

        Truth.assertThat(expected).isInstanceOf(actual::class.java)
        coVerify {
            authDataSource.deleteCurrentUser()
        }
    }


    @Test
    fun sendRecoverPasswordEmail_shouldReturnSuccess() = runTest {
        coEvery {
            authDataSource.sendRecoverPasswordEmail("email")
        } returns Unit

        val expected : Resource<Unit, RemoteFailure> = Resource.Success(Unit)
        val actual = authRepository.sendRecoverPasswordEmail("email")

        Truth.assertThat(actual).isEqualTo(expected)
    }


    fun fakeCurrentUserId(){
        coEvery {
            authDataSource.provideCurrentUser().uid
        } returns "id"
    }
}