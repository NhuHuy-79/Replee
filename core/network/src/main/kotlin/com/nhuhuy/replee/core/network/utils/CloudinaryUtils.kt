package com.nhuhuy.replee.core.network.utils

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils


fun uploadSync(filePath: String): Map<*, *> {
    val config = mapOf(
        "cloud_name" to "your_cloud_name",
        "api_key" to "your_api_key",
        "api_secret" to "your_api_secret"
    )
    val cloudinary = Cloudinary(config)
    // Đây là hàm chạy đồng bộ, nó sẽ block thread hiện tại của Worker
    return cloudinary.uploader().upload(filePath, ObjectUtils.emptyMap())
}