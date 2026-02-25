package com.nhuhuy.replee.feature_auth.utils

import com.nhuhuy.replee.core.common.data.model.ValidateResult
import com.nhuhuy.replee.core.common.utils.InputValidator
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ValidatorTest {
    private lateinit var inputValidator: InputValidator

    @Before
    fun setUp() {
        inputValidator = InputValidator()
    }

    @Test
    fun validateEmail_WhenMatchPattern_ReturnValid(){
        val email = "mm@gmail.com"
        val expected = ValidateResult.Valid
        val actual = inputValidator.validateEmail(email)

        assertEquals(expected, actual)
    }

    @Test
    fun validatePassword_WhenMatchPattern_ReturnValid(){
        val password = "Password20@"
        val expected = ValidateResult.Valid
        val actual = inputValidator.validatePassword(password)

        assertEquals(expected, actual)
    }


}