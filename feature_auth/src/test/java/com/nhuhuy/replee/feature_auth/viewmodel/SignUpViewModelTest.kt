package com.nhuhuy.replee.feature_auth.viewmodel

import app.cash.turbine.test
import com.google.common.truth.Truth
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.utils.ValidateResult
import com.nhuhuy.replee.core.common.utils.Validator
import com.nhuhuy.replee.core.test.DispatcherRuleTest
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import com.nhuhuy.replee.feature_auth.presentation.sign_up.SignUpAction
import com.nhuhuy.replee.feature_auth.presentation.sign_up.SignUpEvent
import com.nhuhuy.replee.feature_auth.presentation.sign_up.SignUpViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SignUpViewModelTest {

    @get:Rule
    val rule = DispatcherRuleTest()

    private lateinit var validator: Validator
    private lateinit var authRepository: AuthRepository
    private lateinit var signUpViewModel: SignUpViewModel

    @Before
    fun setUp(){
        validator = mockk()
        authRepository = mockk()
        signUpViewModel = SignUpViewModel(validator, authRepository)
    }

    @Test
    fun onActionSignUp_ShouldReturnSuccess() = runTest {
        coEvery {
            authRepository.signUpWithEmail("name","email", "password")
        } returns Resource.Success("id")

        every { validator.validateEmail("email") } returns ValidateResult.Valid
        every { validator.validatePassword("password") } returns ValidateResult.Valid
        every { validator.isPasswordConfirmed("password", "password") } returns ValidateResult.Valid

        signUpViewModel.onAction(SignUpAction.OnNameChange("name"))
        signUpViewModel.onAction(SignUpAction.OnEmailChange("email"))
        signUpViewModel.onAction(SignUpAction.OnPasswordChange("password"))
        signUpViewModel.onAction(SignUpAction.OnConfirmPasswordChange("password"))


        signUpViewModel.event.test {
            signUpViewModel.onAction(SignUpAction.SignUp)
            val event = awaitItem()
            Truth.assertThat(event).isEqualTo(SignUpEvent.SignUpSuccessfully)
        }
    }

}