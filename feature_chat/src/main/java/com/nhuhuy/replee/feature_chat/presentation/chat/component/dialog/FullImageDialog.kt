package com.nhuhuy.replee.feature_chat.presentation.chat.component.dialog

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import com.nhuhuy.replee.feature_chat.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BrokenImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.nhuhuy.replee.core.design_system.component.CommonButton

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun FullImageDialog(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    url: String? = null,
) {
    val context = LocalPlatformContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    Column(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
            .sizeIn(maxWidth = screenWidth * 0.5f, maxHeight = screenWidth * 0.7f)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (url == null) {
            ErrorState()
        } else {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(url)
                    .crossfade(true)
                    .build(),

                contentDescription = "Full Image",

                contentScale = ContentScale.Fit,

                modifier = Modifier
                    .fillMaxSize(),

                loading = {
                    CircularProgressIndicator(modifier = Modifier.size(28.dp))
                },

                error = {
                    ErrorState()
                }
            )

            Spacer(Modifier.height(16.dp))

            CommonButton(
                res = R.string.full_img_dialog,
                enabled = true,
                onClick = onClose,
                modifier = Modifier.fillMaxWidth()
            )

        }
    }
}


@Composable
private fun ErrorState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.medium
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.BrokenImage,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}

@Preview
@Composable
private fun FullImageDialogPreview() {
    FullImageDialog(
        onClose = {}
    )
}