package com.nhuhuy.replee.core.firebase

import com.google.firebase.Timestamp

fun Timestamp.toMilliseconds() : Long{
    return this.toDate().time
}