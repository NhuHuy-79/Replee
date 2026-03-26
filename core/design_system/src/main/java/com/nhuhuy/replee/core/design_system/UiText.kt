package com.nhuhuy.replee.core.design_system

import com.nhuhuy.replee.core.common.base.ValidateResult

fun ValidateResult.toUiText(): Int? {
    return when (this) {
        ValidateResult.NameError.TOO_LONG -> R.string.error_name_too_long
        else -> null
    }
}