package com.nhuhuy.replee.core.network.api

import com.google.gson.annotations.SerializedName
import kotlinx.io.IOException

data class ApiError(
    @SerializedName("message")
    val message: String,
    @SerializedName("code")
    val errorCode: Int,
)

data class ApiResponse<out T>(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: T?,
    @SerializedName("apiError")
    val error: ApiError? = null
)

fun <T> ApiResponse<T>.getDataOrThrow(): T {
    if (success) {
        return data ?: throw IOException("Success but data is null")
    } else {
        val errorMessage = error?.message ?: "Unknown server error"
        val errorCode = error?.errorCode ?: -1
        throw IOException("$errorMessage (Code: $errorCode)")
    }
}