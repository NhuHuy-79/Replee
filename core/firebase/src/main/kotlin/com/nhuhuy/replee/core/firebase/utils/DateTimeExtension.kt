package com.nhuhuy.replee.core.firebase.utils

import com.google.firebase.Timestamp

fun Timestamp.toMilliseconds() : Long{
    return this.toDate().time
}