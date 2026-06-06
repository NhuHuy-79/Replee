package com.nhuhuy.replee.core.common

sealed interface UiText {
    data class DynamicString(val value: String) : UiText
    data class StringResource(val resId: Int) : UiText
}
