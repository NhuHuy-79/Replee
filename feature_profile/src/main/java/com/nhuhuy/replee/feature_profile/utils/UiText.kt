package com.nhuhuy.replee.feature_profile.utils

import com.nhuhuy.replee.core.common.base.ValidateResult
import com.nhuhuy.replee.feature_profile.R

fun ValidateResult.toUiText() : Int? {
    return when (this) {
        ValidateResult.Empty -> R.string.empty_input
        ValidateResult.PasswordError.INVALID -> R.string.invalid_password
        ValidateResult.PasswordError.NOT_MATCH -> R.string.password_not_match
        ValidateResult.PasswordError.SAME_AS_OLD -> R.string.password_same_as_old
        else -> null
    }
}