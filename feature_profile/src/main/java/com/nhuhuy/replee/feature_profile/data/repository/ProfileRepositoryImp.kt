package com.nhuhuy.replee.feature_profile.data.repository

import com.nhuhuy.replee.core.common.data.preferences.AppPreferences
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.error_handling.safeCall
import com.nhuhuy.replee.core.common.toRemoteFailure
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.firebase.data_source.FirebaseAuthService
import com.nhuhuy.replee.feature_profile.domain.repository.ProfileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class ProfileRepositoryImp @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val accountLocalDataSource: AccountLocalDataSource,
    private val firebaseAuthService: FirebaseAuthService,
    private val appPreferences: AppPreferences
) : ProfileRepository{
    override suspend fun updatePassword(
        old: String,
        new: String
    ): Resource<Unit, RemoteFailure> {
        return withContext(dispatcher){
            safeCall(
                throwable = { e -> e.toRemoteFailure()}
            ){
                firebaseAuthService.updateNewPassword(old, new)
            }
        }
    }

    override suspend fun logOut() {
        val uid = try {
            firebaseAuthService.provideCurrentUser().uid
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
        firebaseAuthService.logOut()
        appPreferences.setLoggedStatus(false)

        uid?.let {
            uid -> accountLocalDataSource.setLogOut(uid)
        }
    }

}