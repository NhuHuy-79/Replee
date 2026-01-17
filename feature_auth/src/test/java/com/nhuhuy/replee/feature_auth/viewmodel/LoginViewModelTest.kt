package com.nhuhuy.replee.feature_auth.viewmodel

import app.cash.turbine.test
import com.google.common.truth.Truth
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.utils.ValidateResult
import com.nhuhuy.replee.core.common.utils.Validator
import com.nhuhuy.replee.core.test.DispatcherRuleTest
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import com.nhuhuy.replee.feature_auth.presentation.login.LoginAction
import com.nhuhuy.replee.feature_auth.presentation.login.LoginEvent
import com.nhuhuy.replee.feature_auth.presentation.login.LoginViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val rule = DispatcherRuleTest()
    private lateinit var validator: Validator
    private lateinit var authRepository: AuthRepository
    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp(){
        validator = mockk()
        authRepository = mockk()
        loginViewModel = LoginViewModel(validator, authRepository)
    }

    @Test
    fun onActionLogin_ShouldReturnSuccess() = runTest {
        coEvery {
            authRepository.loginWithEmail("email", "password")
        } returns Resource.Success("id")

        every { validator.validateEmail("email") } returns ValidateResult.Valid
        every { validator.validatePassword("password") } returns ValidateResult.Valid

        loginViewModel.onAction(LoginAction.OnEmailChanged("email"))
        loginViewModel.onAction(LoginAction.OnPasswordChanged("password"))


        loginViewModel.event.test {
            loginViewModel.onAction(LoginAction.Login)
            val event = awaitItem()
            Truth.assertThat(event).isEqualTo(LoginEvent.NavigateToHome)
        }
    }
}