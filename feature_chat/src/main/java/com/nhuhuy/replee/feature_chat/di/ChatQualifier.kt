package com.nhuhuy.replee.feature_chat.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ChatScopeId

const val CHAT_SCOPE_ID = "chat_scope_id"
