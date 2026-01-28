package com.nhuhuy.replee.core.common.utils

import timber.log.Timber

interface Logger {
    fun logData(data: String)
    fun logException(throwable: Throwable)
}

class LoggerImp() : Logger {
    override fun logData(data: String) {
        Timber.d(data)
    }

    override fun logException(throwable: Throwable) {
        Timber.e(throwable)
    }

}

