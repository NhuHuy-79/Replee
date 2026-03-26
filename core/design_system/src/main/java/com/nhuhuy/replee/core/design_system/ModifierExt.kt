package com.nhuhuy.replee.core.design_system

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.hideKeyboardOnTap(): Modifier = composed {
    val focusManager = LocalFocusManager.current

    pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)

                if (event.changes.any { it.pressed }) {
                    focusManager.clearFocus()
                }
            }
        }
    }
}