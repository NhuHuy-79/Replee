package com.nhuhuy.replee.feature_chat.presentation.chat.component.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import com.nhuhuy.replee.feature_chat.R
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
fun FullImageDialog(
    url: String?,
    onDismiss: () -> Unit
) {
    val zoomState = rememberZoomState()
    Dialog(
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onDismiss()
                },
            contentAlignment = Alignment.Center
        ) {

            SubcomposeAsyncImage(
                model = url,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .zoomable(zoomState),
                contentScale = ContentScale.Fit,
                onSuccess = {
                    zoomState.setContentSize(it.painter.intrinsicSize)
                },
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                },
                success = {
                    SubcomposeAsyncImageContent()
                },
                error = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.full_img_dialog_error),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    }
}