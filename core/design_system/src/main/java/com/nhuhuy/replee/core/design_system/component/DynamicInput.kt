package com.nhuhuy.replee.core.design_system.component

import androidx.compose.runtime.Immutable
import com.nhuhuy.replee.core.common.utils.ValidateResult

@Immutable
data class DynamicInput(
    val text: String = "",
    val validateResult: ValidateResult = ValidateResult.Idle,
){
    val valid: Boolean get() {
        return validateResult == ValidateResult.Valid
    }
    val error: Boolean get() {
        return validateResult != ValidateResult.Valid && validateResult != ValidateResult.Idle
    }
}