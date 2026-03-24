package com.nhuhuy.replee.feature_auth.repository

import com.google.common.truth.Truth
import com.google.firebase.auth.FirebaseUser
import com.nhuhuy.core.domain.model.AuthenticatedState
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.network.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.network.model.AccountDTO
import com.nhuhuy.replee.core.test.DispatcherRuleTest
import com.nhuhuy.replee.feature_auth.data.repository.AuthRepositoryImp
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyAll
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AuthRepositoryImpTest {
    @get:Rule
    val mainDispatcherRule = DispatcherRuleTest()
    private lateinit var ioDispatcher: CoroutineDispatcher
    private lateinit var accountNetworkDataSource: AccountNetworkDataSource
    private lateinit var firebaseAuthEmailService: FirebaseAuthEmailService
    private lateinit var googleAuthService: GoogleAuthService
    private lateinit var accountLocalDataSource: AccountLocalDataSource
    private lateinit var authRepositoryImp: AuthRepositoryImp

    @Before
    fun setUp() {
        ioDispatcher = StandardTestDispatcher(TestScope().testScheduler)
        firebaseAuthEmailService = mockk(relaxed = true)
        accountNetworkDataSource = mockk(relaxed = true)
        accountLocalDataSource = mockk(relaxed = true)
        googleAuthService = mockk(relaxed = true)


    }

    @Test
    fun `should return success when logic with email`() = runTest {
        // Arrange
        coEvery { firebaseAuthEmailService.getCurrentUser()?.uid } returns "uid"
        coEvery { firebaseAuthEmailService.loginWithEmail("email", "password") } just Runs
        coEvery { accountNetworkDataSource.fetchAccountById(any()) } returns mockk(relaxed = true)
        coEvery { accountLocalDataSource.upsertAccount(any()) } just Runs
        coEvery { firebaseAuthEmailService.getDeviceToken() } returns "token"
        coEvery {
            accountNetworkDataSource.updateDeviceToken(any(), eq("token"))
        } just Runs
        //Act
        val action = authRepositoryImp.loginWithEmail("email", "password")

        //Assert
        Truth.assertThat(action).isInstanceOf(NetworkResult::class.java)
        Truth.assertThat(action).isEqualTo(NetworkResult.Success("uid"))

        //Verify
        coVerifyAll {
            firebaseAuthEmailService.getCurrentUser()?.uid
            firebaseAuthEmailService.loginWithEmail("email", "password")
            accountNetworkDataSource.fetchAccountById(any())
            accountLocalDataSource.upsertAccount(any())
            firebaseAuthEmailService.getDeviceToken()
            accountNetworkDataSource.updateDeviceToken(any(), "token")
        }
    }

    @Test
    fun `signInWithGoogle should return account`() = runTest {

        val dto = AccountDTO(
            id = "uid",
            name = "name",
            email = "email"
        )

        coEvery { googleAuthService.signIn("token") } returns dto

        val result = authRepositoryImp.signInWithGoogle("token")

        Truth.assertThat(result).isInstanceOf(NetworkResult.Success::class.java)

        val account = (result as NetworkResult.Success).data

        Truth.assertThat(account.id).isEqualTo("uid")

        coVerify { googleAuthService.signIn("token") }
    }

    @Test
    fun `signUpWithEmail should create account`() = runTest {

        val user = mockk<FirebaseUser>()
        every { user.uid } returns "uid"

        coEvery { firebaseAuthEmailService.signUpWithEmail("email", "password") } just Runs
        coEvery { firebaseAuthEmailService.getCurrentUser() } returns user
        coEvery { firebaseAuthEmailService.getDeviceToken() } returns "token"

        coEvery { accountNetworkDataSource.sendAccount(any()) } just Runs

        val result = authRepositoryImp.signUpWithEmail(
            name = "name",
            email = "email",
            password = "password"
        )

        Truth.assertThat(result).isInstanceOf(NetworkResult.Success::class.java)

        coVerify {
            accountNetworkDataSource.sendAccount(any())
            accountLocalDataSource.upsertAccount(any())
            accountNetworkDataSource.updateDeviceToken("uid", "token")
        }
    }

    @Test
    fun `sendRecoverPasswordEmail should call service`() = runTest {

        coEvery { firebaseAuthEmailService.sendRecoverPasswordEmail("email") } just Runs

        val result = authRepositoryImp.sendRecoverPasswordEmail("email")

        Truth.assertThat(result).isEqualTo(NetworkResult.Success(Unit))

        coVerify { firebaseAuthEmailService.sendRecoverPasswordEmail("email") }
    }

    @Test
    fun `observeAuthState should emit uid`() = runTest {

        val flow = flowOf("uid")

        every { firebaseAuthEmailService.observeAuthState() } returns flow

    }

    @Test
    fun `observeAuthenticationState should emit state`() = runTest {

        val flow = flowOf(AuthenticatedState.Authenticated(uid = "uid"))

        every { firebaseAuthEmailService.authState() } returns flow

    }

}