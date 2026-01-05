package com.nhuhuy.replee.feature_profile

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import app.cash.turbine.test
import com.google.common.truth.Truth
import com.nhuhuy.replee.core.test.DispatcherRuleTest
import com.nhuhuy.replee.feature_profile.data.data_store.NotificationMode
import com.nhuhuy.replee.feature_profile.data.data_store.SettingDataStore
import com.nhuhuy.replee.feature_profile.data.data_store.SettingDataStoreImp
import dagger.hilt.android.qualifiers.ApplicationContext
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingDataStoreTest {
    @get:Rule
    val rule = DispatcherRuleTest()

    private lateinit var context: Context

    private lateinit var settingDataStore: SettingDataStore

    @Before
    fun setUp(){
        settingDataStore = SettingDataStoreImp(context.applicationContext)
    }

    @Test
    fun updateNotification_shouldReturnSuccess() = runTest {
        coEvery {
            context.applicationContext

        } returns context
        val mode: NotificationMode = NotificationMode.PRIVATE
        settingDataStore.updateNotification(mode)

        settingDataStore.observeNotification().test {
            val actual = awaitItem()
            Truth.assertThat(actual).isEqualTo(mode)
        }
    }
}