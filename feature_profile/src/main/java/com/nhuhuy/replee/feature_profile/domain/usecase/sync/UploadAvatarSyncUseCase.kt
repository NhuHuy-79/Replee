package com.nhuhuy.replee.feature_profile.domain.usecase.sync

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.repository.AccountRepository
import com.nhuhuy.core.domain.repository.FileRepository
import com.nhuhuy.replee.core.data.utils.flatMap
import javax.inject.Inject

class UploadAvatarSyncUseCase @Inject constructor(
    private val fileRepository: FileRepository,
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(uid: String): NetworkResult<String> {
        val localFilePath = fileRepository.getUriPathWithUserId(uid)
            ?: return NetworkResult.Failure(Exception("Cannot found local file path!"))
        return fileRepository.uploadImageWithOption(
            uriPath = localFilePath.localPath,
            folder = "replee/avatars",
            option = mapOf(
                "public_id" to "avatar_${uid}",
                "overwrite" to "true",
                "quality" to "auto",
                "fetch_format" to "auto",
                "transformation" to "w_500,h_500,c_fill,g_face"
            )
        ).flatMap { url -> accountRepository.updateUserImage(uid = uid, remoteUrl = url) }
    }
}
