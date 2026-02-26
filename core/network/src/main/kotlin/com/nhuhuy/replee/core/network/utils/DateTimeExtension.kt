package com.nhuhuy.replee.core.network.utils

import com.google.firebase.Timestamp

fun Timestamp.toMilliseconds() : Long{
    return this.toDate().time
}