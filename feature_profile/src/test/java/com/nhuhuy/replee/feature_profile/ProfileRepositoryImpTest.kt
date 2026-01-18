package com.nhuhuy.replee.feature_profile

import com.nhuhuy.replee.core.common.data.preferences.AppPreferences
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.firebase.data_source.FirebaseAuthService
import com.nhuhuy.replee.core.test.DispatcherRuleTest
import com.nhuhuy.replee.feature_profile.data.repository.ProfileRepositoryImp
import com.nhuhuy.replee.feature_profile.domain.repository.ProfileRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProfileRepositoryImpTest {
    @get:Rule
    val main = DispatcherRuleTest()

    private lateinit var firebaseAuthService: FirebaseAuthService
    private lateinit var accountLocalDataSource: AccountLocalDataSource
    private lateinit var repositoryImp: ProfileRepository

    private lateinit var appPreferences: AppPreferences

    @Before
    fun setUp(){
        accountLocalDataSource = mockk(relaxed = true)
        firebaseAuthService = mockk(relaxed = true)
        appPreferences = mockk(relaxed = true)
        repositoryImp = ProfileRepositoryImp(
            dispatcher = Dispatchers.IO,
            firebaseAuthService = firebaseAuthService,
            accountLocalDataSource = accountLocalDataSource,
            appPreferences = appPreferences
        )
    }

    @Test
    fun returnSuccess_whenLogOut() = runTest {
        coEvery {
            firebaseAuthService.getCurrentUser().uid
        } returns "uid"

        coEvery {
            firebaseAuthService.logOut()
        } returns Unit

        coEvery {
            appPreferences.saveLoggedStatus(false)
        } returns Unit

        coEvery {
            accountLocalDataSource.updateLogoutStatus("uid")
        } returns Unit

        val actual = repositoryImp.logOut()
        assert(actual == Unit)
    }

    @Test
    fun returnSuccess_whenUpdateNewPassword() = runTest {
        val expected : Resource<Unit, RemoteFailure> = Resource.Success(Unit)

        coEvery {
            firebaseAuthService.updateNewPassword("old", "new")
        } returns Unit

        val actual = repositoryImp.updateNewPassword("old", "new")

        assert(expected == actual)
    }

}