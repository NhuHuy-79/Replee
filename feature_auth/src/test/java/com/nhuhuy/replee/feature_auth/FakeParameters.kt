package com.nhuhuy.replee.feature_auth

import com.nhuhuy.core.domain.model.Account

class FakeParameters {
    companion object {
        val fakeAccount = Account(
            id = "123",
            name = "nhuhuy",
            email = "nhuhuy@gmail.com"
        )
        val fakeException = Exception("Test Exception")

        const val FAKE_TOKEN = "123"
    }
}
