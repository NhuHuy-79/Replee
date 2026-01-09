package com.nhuhuy.replee.feature_profile

import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
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
    private lateinit var repositoryImp: ProfileRepository

    @Before
    fun setUp(){
        firebaseAuthService = mockk(relaxed = true)
        repositoryImp = ProfileRepositoryImp(
            dispatcher = Dispatchers.IO,
            firebaseAuthService = firebaseAuthService
        )
    }

    @Test
    fun returnSuccess_whenUpdatePassword() = runTest {
        val expected : Resource<Unit, RemoteFailure> = Resource.Success(Unit)

        coEvery {
            firebaseAuthService.updateNewPassword("old", "new")
        } returns Unit

        val actual = repositoryImp.updatePassword("old", "new")

        assert(expected == actual)
    }

}