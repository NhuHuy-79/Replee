package com.nhuhuy.replee.feature_auth.repository

import com.google.common.truth.Truth
import com.nhuhuy.replee.core.common.data.AccountDataSource
import com.nhuhuy.replee.core.common.data.model.Account
import com.nhuhuy.replee.core.common.error_handling.FirestoreDataNotFoundException
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.feature_auth.data.repository.AuthRepositoryImp
import com.nhuhuy.replee.feature_auth.data.source.AuthDataSource
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
    private lateinit var accountDataSource: AccountDataSource
    private lateinit var authDataSource: AuthDataSource

    @Before
    fun setUp() {
        authDataSource = mockk()
        accountDataSource = mockk()
        authRepository = AuthRepositoryImp(accountDataSource,authDataSource, dispatcher = Dispatchers.IO)
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
        val expected: Resource<String, RemoteFailure> = Resource.Failure(RemoteFailure.Unknown)
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
            val account = Account(
                id = "id",
                name = "name",
                email = "email",
            )
            accountDataSource.addAccount(account)
        } returns Unit

        val expected : Resource<String, RemoteFailure> = Resource.Success("id")
        val actual = authRepository.signUpWithEmail("name","email", "password")

        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun signUpWithEmail_whenThrowException_shouldReturnRemoteFailure() = runTest {
        val exception = FirestoreDataNotFoundException()
        fakeCurrentUserId()
        coEvery {
            authDataSource.signUpWithEmail("email", "password")
        } returns Unit
        coEvery {
            val account = Account(
                id = "id",
                name = "name",
                email = "email",
            )
            accountDataSource.addAccount(account)
        } throws FirestoreDataNotFoundException()

        val expected: Resource<String, RemoteFailure> = Resource.Failure(RemoteFailure.Unknown)
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
            authDataSource.currentUser.uid
        } returns "id"
    }
}