package com.nhuhuy.replee.feature_auth.utils

import com.nhuhuy.replee.core.common.data.model.ValidateResult
import com.nhuhuy.replee.core.common.utils.Validator
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ValidatorTest {
    private lateinit var validator: Validator

    @Before
    fun setUp() {
        validator = Validator()
    }

    @Test
    fun validateEmail_WhenMatchPattern_ReturnValid(){
        val email = "mm@gmail.com"
        val expected = ValidateResult.Valid
        val actual = validator.validateEmail(email)

        assertEquals(expected, actual)
    }

    @Test
    fun validatePassword_WhenMatchPattern_ReturnValid(){
        val password = "Password20@"
        val expected = ValidateResult.Valid
        val actual = validator.validatePassword(password)

        assertEquals(expected, actual)
    }


}