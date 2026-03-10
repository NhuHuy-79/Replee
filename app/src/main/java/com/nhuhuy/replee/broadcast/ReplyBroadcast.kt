package com.nhuhuy.replee.broadcast

import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.nhuhuy.replee.notification.EXTRA_NOTIFICATION_ID
import com.nhuhuy.replee.notification.EXTRA_RECEIVER_ID
import com.nhuhuy.replee.notification.EXTRA_SENDER_ID
import com.nhuhuy.replee.notification.REMOTE_INPUT_KEY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ReplyBroadcast() : BroadcastReceiver() {
    @Inject
    lateinit var handler: BroadcastDataMapper
    private lateinit var notificationManager: NotificationManagerCompat

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context?, intent: Intent?){
        if (context == null || intent == null) {
            Timber.e("Context or Intent is null")
            return
        }

        notificationManager = NotificationManagerCompat.from(context)

        val senderId = intent.getStringExtra(EXTRA_SENDER_ID)
        val receiverId = intent.getStringExtra(EXTRA_RECEIVER_ID)
        val conversationId = intent.getStringExtra(com.nhuhuy.replee.notification.EXTRA_CONVERSATION_ID)
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)

        val pendingResult = goAsync()
        val replyText = RemoteInput.getResultsFromIntent(intent).getCharSequence(REMOTE_INPUT_KEY)

        if (replyText == null) {
            Timber.e("Reply text is null")
            pendingResult.finish()
            return
        }

        scope.launch {
            try {
                val input = replyText.toString()
                if (senderId != null && receiverId != null && conversationId != null) {
                    handler.handleReplyMessage(
                        conversationId = conversationId,
                        senderId = senderId,
                        receiverId = receiverId,
                        message = input
                    )
                    handler.showNotification(true)
                }
                else {
                    Timber.e("senderId: $senderId - receiverId: $receiverId - conversationId: $conversationId")
                }

            } catch (e: Exception) {
                Timber.e(e)
                //TODO("Add to sync data")
                handler.showNotification(false)
            } finally {
                notificationManager.cancel(notificationId)
                pendingResult.finish()
            }
        }
    }

}