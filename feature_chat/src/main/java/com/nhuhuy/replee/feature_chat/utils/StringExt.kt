package com.nhuhuy.replee.feature_chat.utils

fun String.getMainName(): String {
    if (this.isBlank()) return ""
    val str = this.split(" ")

    if (str.size == 1) {
        return str.firstOrNull().orEmpty()
    } else {
        return str.lastOrNull().orEmpty()
    }
}