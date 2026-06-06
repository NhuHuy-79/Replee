package com.nhuhuy.replee.core.network.api.file

import com.nhuhuy.replee.core.network.api.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST

data class DeleteImageRequest(
    val publicId: String
)

interface FileApi {

    @POST("api/v1/images/delete")
    suspend fun deleteFile(
        @Body request: DeleteImageRequest
    ): ApiResponse<String>
}
