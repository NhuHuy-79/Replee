package com.nhuhuy.replee.feature_auth.viewmodel

import com.nhuhuy.replee.core.common.base.ValidateResult
import com.nhuhuy.replee.core.common.utils.InputValidator
import com.nhuhuy.replee.core.test.DispatcherRuleTest
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import com.nhuhuy.replee.feature_auth.presentation.login.LoginAction
import com.nhuhuy.replee.feature_auth.presentation.login.LoginViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val rule = DispatcherRuleTest()
    private lateinit var inputValidator: InputValidator
    private lateinit var authRepository: AuthRepository
    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp(){
        inputValidator = mockk()
        authRepository = mockk()


        @Test
    fun onActionLogin_ShouldReturnSuccess() = runTest {

            every { inputValidator.validateEmail("email") } returns ValidateResult.Valid
            every { inputValidator.validatePassword("password") } returns ValidateResult.Valid

            loginViewModel.onAction(LoginAction.OnEmailChanged("email"))
            loginViewModel.onAction(LoginAction.OnPasswordChanged("password"))

        }

    }
}