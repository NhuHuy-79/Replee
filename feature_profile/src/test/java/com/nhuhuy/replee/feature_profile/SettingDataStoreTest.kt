package com.nhuhuy.replee.feature_profile

import android.content.Context
import app.cash.turbine.test
import com.google.common.truth.Truth
import com.nhuhuy.replee.core.common.data.data_store.AppDataStore
import com.nhuhuy.replee.core.common.data.data_store.AppDataStoreImp
import com.nhuhuy.replee.core.common.data.data_store.NotificationMode
import com.nhuhuy.replee.core.test.DispatcherRuleTest
import io.mockk.coEvery
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

    private lateinit var appDataStore: AppDataStore

    @Before
    fun setUp(){
        appDataStore = AppDataStoreImp(context.applicationContext)
    }

    @Test
    fun saveNotification_Mode_shouldReturnSuccess() = runTest {
        coEvery {
            context.applicationContext

        } returns context
        val mode: NotificationMode = NotificationMode.PRIVATE
        appDataStore.saveNotificationMode(mode)

        appDataStore.observeNotification().test {
            val actual = awaitItem()
            Truth.assertThat(actual).isEqualTo(mode)
        }
    }
}