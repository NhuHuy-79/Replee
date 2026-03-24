package com.nhuhuy.replee.core.network.api.cloudinary

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface CloudinaryApi {
    @Multipart
    @POST("dgq6g8u5h/image/upload")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("upload_preset") uploadPreset: RequestBody
    ): Response<CloudinaryResponse>

    @Multipart
    @POST("{cloud_name}/image/upload")
    suspend fun uploadImage(
        @Path("cloud_name") cloudName: String,
        @Part file: MultipartBody.Part,
        @Part("upload_preset") preset: RequestBody,
        @Part("folder") folder: RequestBody, // Thêm cái này để chia folder
        @Part("quality") quality: RequestBody? = null // Thêm nén để tiết kiệm 5GB
    ): Response<CloudinaryResponse>
}

