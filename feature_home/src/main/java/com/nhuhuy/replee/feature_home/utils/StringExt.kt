package com.nhuhuy.replee.feature_home.utils

fun String.getMainName(): String {
    if (this.isBlank()) return ""
    val str = this.split(" ")

    return if (str.size == 1) {
        str.firstOrNull().orEmpty()
    } else {
        str.lastOrNull().orEmpty()
    }
}
