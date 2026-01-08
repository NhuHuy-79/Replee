package com.nhuhuy.replee.core.firebase

object Constant{

    object Room {
        const val ACCOUNT_TABLE = "accounts"
        const val CONVERSATION_TABLE = "conversations"
        const val MESSAGE_TABLE = "messages"
    }

    object Firestore{
        const val USER_COLLECTION = "users"
        const val CONVERSATION_COLLECTION = "conversations"
        const val MESSAGE_COLLECTION = "message_by_conversationIds"

        const val MESSAGE_SUBCOLLECTION = "messages"
    }
}