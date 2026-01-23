package com.nhuhuy.replee.feature_chat.utils

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await

suspend fun DocumentReference.unionFieldValueInArray(
    fieldName: String,
    fieldValue: Any
) {
    this.update(fieldName, FieldValue.arrayUnion(fieldValue)).await()
}

suspend fun DocumentReference.removeFieldValueInArray(
    fieldName: String,
    fieldValue: Any
) {
    this.update(fieldName, FieldValue.arrayRemove(fieldValue)).await()
}


suspend fun DocumentReference.updateFieldValue(
    fieldName: String,
    fieldValue: Any
) {
    update(fieldName, fieldValue).await()
}

suspend fun DocumentReference.updateFieldValue(
    data: Map<String, Any>
) {
    update(data).await()
}

