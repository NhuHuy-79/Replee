package com.nhuhuy.replee.feature_auth

import com.nhuhuy.core.domain.model.Account

class FakeParameters {
    companion object {
        val fakeAccount: Account = Account(
            id = "123",
            name = "Như Huy",
            email = "email",
        )

        const val FAKE_TOKEN = "token"
        val fakeException = Exception("Fake Exception")

    }
}