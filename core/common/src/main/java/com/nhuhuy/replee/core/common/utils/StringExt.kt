package com.nhuhuy.replee.core.common.utils

fun extractPublicId(cloudinaryUrl: String): String? {
    try {
        var path = cloudinaryUrl.substringAfter("/upload/", missingDelimiterValue = "")
        if (path.isEmpty()) return null

        if (path.matches(Regex("^v\\d+/.*"))) {
            path = path.substringAfter("/")
        }

        val publicId = path.substringBeforeLast(".")
        return publicId
    } catch (e: Exception) {
        return null
    }
}