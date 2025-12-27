package com.nhuhuy.replee.core.common.error_handling

sealed interface Resource<out D, out F: Failure> {
    data class Success<out D, out F: com.nhuhuy.replee.core.common.error_handling.Failure>(val data: D): Resource<D, F>
    data class Failure<out D, out F: com.nhuhuy.replee.core.common.error_handling.Failure>(val error: F): Resource<D, F>
}


