package com.nhuhuy.replee.di

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import com.nhuhuy.replee.core.common.di.ScopeHolder

@SuppressLint("ContextCastToActivity")
@Composable
fun HiltScopedComposable(
    scopeName: String,
    scopeHolder: ScopeHolder,
    content: @Composable () -> Unit,
) {
    val activity = LocalContext.current as? Activity

    DisposableEffect(scopeName) {
        onDispose {
            if (activity?.isChangingConfigurations != true) {
                scopeHolder.releaseScope(scopeName)
            }
        }
    }

    content()
}