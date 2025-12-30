package com.nhuhuy.replee.feature_auth.presentation.shared

import androidx.annotation.StringRes
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun NormalTextField(
    modifier: Modifier,
    @StringRes label: Int,
    leadingIcon: @Composable () -> Unit,
    @StringRes errorText: Int?,
    dynamicInput: DynamicInput,
    onValueChange: (value: String )-> Unit,
){
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    OutlinedTextField(
        modifier = modifier
            .bringIntoViewRequester(bringIntoViewRequester)
            .onFocusEvent { state ->
                if (state.isFocused)
                    coroutineScope.launch {
                        bringIntoViewRequester.bringIntoView()
                    }

            },
        value = dynamicInput.text,
        onValueChange = onValueChange,
        shape = RoundedCornerShape(12.dp),
        isError = dynamicInput.error,
        label = {
            Text(
                text = stringResource(label),
                style = MaterialTheme.typography.labelLarge
            )
        },
        singleLine = true,
        leadingIcon = leadingIcon,
        supportingText = {
            errorText?.let { id ->
                Text(
                    text = stringResource(id),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    )
}

