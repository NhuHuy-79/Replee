package com.nhuhuy.replee.core.common.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.showShortToast(
    @StringRes message: Int,
) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showLongToast(
    @StringRes message: Int,
) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}