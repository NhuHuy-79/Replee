package com.nhuhuy.replee.core.design_system.component

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NormalTextField(
    modifier: Modifier,
    @StringRes label: Int,
    leadingIcon: @Composable () -> Unit,
    @StringRes errorText: Int?,
    validatableInput: ValidatableInput,
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
        value = validatableInput.text,
        onValueChange = onValueChange,
        shape = RoundedCornerShape(12.dp),
        isError = validatableInput.error,
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

