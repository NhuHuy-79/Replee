package com.nhuhuy.replee.core.network.utils

import com.google.firebase.Timestamp
import java.util.Date

fun Timestamp.toMilliseconds() : Long{
    return this.toDate().time
}

fun Long.toTimestamp(): Timestamp {
    return Timestamp(Date(this))
}
