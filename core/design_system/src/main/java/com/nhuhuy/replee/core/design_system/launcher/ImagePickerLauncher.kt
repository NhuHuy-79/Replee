package com.nhuhuy.replee.core.design_system.launcher

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

@Composable
fun rememberImagePicker(
    onImagePicked: (Uri?) -> Unit
): ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?> {

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        onImagePicked(uri)
    }

    return launcher
}