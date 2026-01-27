package com.nhuhuy.replee.core.common.utils

import com.nhuhuy.replee.core.common.data.model.ValidateResult

class Validator() {
    private val passwordMinLength: Int = 8

    fun validateEmail(email: String): ValidateResult {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        return when {
            email.isEmpty() -> ValidateResult.Empty
            emailRegex.toRegex().matches(email) -> ValidateResult.Valid
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

    fun isPasswordConfirmed(password: String, confirmedPassword: String): ValidateResult {
        return if (password == confirmedPassword) ValidateResult.Valid else ValidateResult.PasswordError.NOT_MATCH
    }

    fun validateNickName(name: String): ValidateResult {
        return when {
            name.length > 100 -> ValidateResult.NameError.TOO_LONG
            else -> ValidateResult.Valid
        }
    }

    fun validateNewPassword(old: String, new: String): ValidateResult {
        return when {
            new.isEmpty() -> ValidateResult.Empty
            old == new -> ValidateResult.PasswordError.SAME_AS_OLD
            else -> ValidateResult.PasswordError.INVALID
        }
    }
}