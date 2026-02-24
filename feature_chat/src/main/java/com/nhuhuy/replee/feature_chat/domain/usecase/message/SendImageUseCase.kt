package com.nhuhuy.replee.feature_chat.domain.usecase.message

import javax.inject.Inject

class SendImageUseCase @Inject constructor() {
    suspend operator fun invoke(uriPath: String) {
        //Parse URi to File
        //Repository to Send File with Cloudinary
    }
}