package com.nhuhuy.replee.feature_auth.presentation.shared

import androidx.compose.runtime.Immutable
import com.nhuhuy.replee.feature_auth.domain.model.ValidateResult

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