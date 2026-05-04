package com.nhuhuy.replee.core.sync.domain.usecase.message

import com.nhuhuy.replee.core.domain.SessionManager
import com.nhuhuy.replee.core.domain.repository.ConversationRepository
import com.nhuhuy.replee.core.domain.repository.FileRepository
import com.nhuhuy.replee.core.domain.repository.MessageRepository
import com.nhuhuy.replee.core.domain.repository.PushNotificationRepository
import com.nhuhuy.replee.core.model.chat.MessageStatus
import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import com.nhuhuy.replee.core.model.error_handling.onFailure
import com.nhuhuy.replee.core.model.error_handling.onSuccess
import javax.inject.Inject

class SendFileSyncUseCase @Inject constructor(
    private val sessionManager: SessionManager,
    private val fileRepository: FileRepository,
    private val messageRepository: MessageRepository,
    private val conversationRepository: ConversationRepository,
    private val pushNotificationRepository: PushNotificationRepository
) {
    suspend operator fun invoke(
        messageId: String,
        onUpdateStatus: suspend (String, MessageStatus) -> Unit,
        onSyncConversationFailure: suspend () -> Unit
    ): NetworkResult<Unit> {
        val filePath = fileRepository.getUriPathWithMessageId(messageId)
            ?: return NetworkResult.Failure(Exception("Uri Path is null"))

        sessionManager.getUserIdOrNull()
            ?: return NetworkResult.Failure(Exception("User not logged in"))

        val uploadFileResult = fileRepository.uploadFile(uriPath = filePath.localPath)

        return when (uploadFileResult) {
            is NetworkResult.Failure -> uploadFileResult
            is NetworkResult.Success -> {
                val remoteUrl = uploadFileResult.data
                val message = messageRepository.updateRemoteUrlMessage(
                    messageId = messageId,
                    remoteUrl = remoteUrl,
                    status = MessageStatus.PENDING
                ) ?: return NetworkResult.Failure(Exception("Failed to update remote URL"))

                when (val sendResult = messageRepository.sendMessage(message)) {
                    is NetworkResult.Success -> {
                        onUpdateStatus(messageId, MessageStatus.SYNCED)
                        pushNotificationRepository.pushNotification(message)
                        conversationRepository.updateMetadataConversation(message)
                            .onSuccess {
                                // Handled by caller if needed, but here we update status
                                // In the original worker it called syncManager.updateConversationStatus
                            }
                            .onFailure {
                                onSyncConversationFailure()
                            }
                        NetworkResult.Success(Unit)
                    }

                    is NetworkResult.Failure -> sendResult
                }
            }
        }
    }
}
