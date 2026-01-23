package com.nhuhuy.replee.feature_chat.data.model.network

import com.google.firebase.firestore.FieldValue

data class ConversationPatch(
    val id: String,
    val mapFieldValue: Map<String, FieldValue>,
    val mapLastMessage: Map<String, Any?>,
)
