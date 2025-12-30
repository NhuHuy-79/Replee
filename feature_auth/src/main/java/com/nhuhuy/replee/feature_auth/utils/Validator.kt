package com.nhuhuy.replee.feature_auth.utils

import android.util.Patterns
import com.nhuhuy.replee.feature_auth.domain.model.ValidateResult

class Validator() {
    private val passwordMinLength: Int = 8

    fun validateEmail(email: String): ValidateResult {
        return when {
            email.isEmpty() -> ValidateResult.Empty
            Patterns.EMAIL_ADDRESS.matcher(email).matches() -> ValidateResult.Valid
            else -> ValidateResult.EmailError.INVALID
        }
    }

    fun validatePassword(password: String): ValidateResult {
        return when {
            password.isEmpty() -> ValidateResult.Empty
            isValidPassword(password) -> ValidateResult.Valid
            else -> ValidateResult.PasswordError.INVALID
        }
    }

    private fun isValidPassword(password: String): Boolean {
        val hasDigit = password.any { it.isDigit() }
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }

        return hasSpecialChar && hasUpperCase && hasLowerCase && hasDigit && password.length >= passwordMinLength
    }

    fun isPasswordConfirmed(password: String, confirmedPassword: String): ValidateResult{
        return if (password == confirmedPassword) ValidateResult.Valid else ValidateResult.PasswordError.NOT_MATCH

    }
}