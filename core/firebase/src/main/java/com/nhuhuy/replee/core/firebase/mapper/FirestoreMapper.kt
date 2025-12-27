package com.nhuhuy.replee.core.firebase.mapper

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure

fun FirebaseFirestoreException.toRemoteFailure() : RemoteFailure {
    return when (this.code) {
        FirebaseFirestoreException.Code.INVALID_ARGUMENT -> TODO()
        FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> TODO()
        FirebaseFirestoreException.Code.NOT_FOUND -> TODO()
        FirebaseFirestoreException.Code.ALREADY_EXISTS -> TODO()
        FirebaseFirestoreException.Code.PERMISSION_DENIED -> TODO()
        FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> TODO()
        FirebaseFirestoreException.Code.FAILED_PRECONDITION -> TODO()
        FirebaseFirestoreException.Code.ABORTED -> TODO()
        FirebaseFirestoreException.Code.OUT_OF_RANGE -> TODO()
        FirebaseFirestoreException.Code.UNIMPLEMENTED -> TODO()
        FirebaseFirestoreException.Code.INTERNAL -> TODO()
        FirebaseFirestoreException.Code.UNAVAILABLE -> TODO()
        FirebaseFirestoreException.Code.DATA_LOSS -> TODO()
        FirebaseFirestoreException.Code.UNAUTHENTICATED -> TODO()
        else -> RemoteFailure.Unknown
    }
}