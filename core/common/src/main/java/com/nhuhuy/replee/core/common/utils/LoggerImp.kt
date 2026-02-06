package com.nhuhuy.replee.core.common.utils

import com.nhuhuy.core.domain.utils.Logger
import timber.log.Timber

class LoggerImp() : Logger {
    override fun logData(data: String) {
        Timber.d(data)
    }

    override fun logException(throwable: Throwable) {
        Timber.e(throwable)
    }

}