package com.nhuhuy.replee.core.design_system.component

import androidx.annotation.StringRes
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun SecureTextField(
    modifier: Modifier,
    @StringRes label: Int,
    leadingIcon: @Composable () -> Unit,
    @StringRes errorText: Int?,
    dynamicInput: DynamicInput,
    onValueChange: (value: String )-> Unit,
){
    var visibility by remember { mutableStateOf(false) }
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
        singleLine = true,
        value = dynamicInput.text,
        shape = RoundedCornerShape(12.dp),
        onValueChange = onValueChange,
        visualTransformation = if (visibility) VisualTransformation.None else PasswordVisualTransformation(),
        isError = dynamicInput.error,
        label = {
            Text(
                text = stringResource(label),
                style = MaterialTheme.typography.labelLarge
            )
        },
        leadingIcon = leadingIcon,
        trailingIcon = {
            IconButton(
                onClick = { visibility = !visibility }
            ) {
                Icon(
                    imageVector = if (!visibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = null
                )
            }

        },
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