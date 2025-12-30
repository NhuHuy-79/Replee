package com.nhuhuy.replee.core.common

import com.nhuhuy.replee.core.common.error_handling.Failure
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.error_handling.mapSuccess
import com.nhuhuy.replee.core.common.error_handling.onSuccess
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ExtensionTest {

    private lateinit var success : Resource<String, Failure>
    private lateinit var failure: Resource<String, Failure>

    @Before
    fun setUp(){
        success = Resource.Success("Success")
        failure = Resource.Failure(RemoteFailure.Network)
    }

    @Test
    fun shouldReturnString_whenSuccess(){
        val expected = "Success"
        var actual = ""
        success.onSuccess { value ->
            actual = value
        }
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun shouldReturnMappedString_whenMapSuccess(){
        val expected = "Mapped Success"
        var actual = ""
        success.mapSuccess {
            actual = "Mapped $it"
        }
        Assert.assertEquals(expected, actual)
    }
}