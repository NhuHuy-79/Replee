package com.nhuhuy.replee.feature_profile

import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.core.domain.model.AuthServiceProvider
import java.util.UUID

class FakeParameters {
    companion object {
        val fakeAccount = Account(
            id = "uid_123",
            name = "Test User",
            email = "test@example.com",
            imageUrl = "https://example.com/image.jpg",
            online = true,
            lastActive = System.currentTimeMillis(),
            currentToken = "token_123",
            provider = AuthServiceProvider.EMAIL
        )

        val fakeUuid: UUID = UUID.randomUUID()
        val fakeException = Exception("Fake profile exception")
        const val fakeUriPath = "content://media/external/images/media/1"
        const val fakeInternalPath = "/data/user/0/com.nhuhuy.replee/files/temp.jpg"
    }
}
