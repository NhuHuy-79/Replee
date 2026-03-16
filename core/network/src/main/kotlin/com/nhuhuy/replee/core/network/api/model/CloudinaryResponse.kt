package com.nhuhuy.replee.core.network.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CloudinaryResponse(
    @SerialName("secure_url") val secureUrl: String,
    @SerialName("public_id") val publicId: String
)