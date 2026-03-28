package com.nhuhuy.replee.core.network.api.cloudinary

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface CloudinaryApi {
    /**
     * Upload image to Cloudinary using dynamic cloud name.
     *
     * @param cloudName The cloud name from Cloudinary dashboard.
     * @param file The file to upload.
     * @param uploadPreset The upload preset name.
     * @param folder Optional folder name to store the image.
     * @param quality Optional quality setting (e.g., "auto").
     */
    @Multipart
    @POST("{cloud_name}/image/upload")
    suspend fun uploadImage(
        @Path("cloud_name") cloudName: String,
        @Part file: MultipartBody.Part,
        @Part("upload_preset") uploadPreset: RequestBody,
        @Part("folder") folder: RequestBody? = null,
        @Part("quality") quality: RequestBody? = null
    ): Response<CloudinaryResponse>
}
