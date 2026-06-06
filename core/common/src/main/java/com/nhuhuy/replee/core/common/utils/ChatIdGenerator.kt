package com.nhuhuy.replee.core.common.utils

object ChatIdGenerator {
    fun generate(
        uid1: String,
        uid2: String,
    ): String {
        return listOf(uid1, uid2).sorted().joinToString(separator = "_")
    }
}