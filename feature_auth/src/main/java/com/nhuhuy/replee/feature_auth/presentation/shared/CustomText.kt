package com.nhuhuy.replee.feature_auth.presentation.shared

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

@Composable
fun customText(
    @StringRes first: Int,
    @StringRes second: Int,
    firstStyle: SpanStyle,
    secondStyle: SpanStyle
) = buildAnnotatedString {
    withStyle(style = firstStyle){
        append(
            text = stringResource(first)
        )
    }
    append(" ")
    withStyle(style = secondStyle){
        append(
            text = stringResource(second)
        )
    }

}