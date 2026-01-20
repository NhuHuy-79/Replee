package com.nhuhuy.replee.feature_chat.presentation.setting.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.core.design_system.component.CommonButton
import com.nhuhuy.replee.core.design_system.component.DynamicInput
import com.nhuhuy.replee.core.design_system.component.NormalTextField
import com.nhuhuy.replee.core.design_system.component.SheetContainer
import com.nhuhuy.replee.core.design_system.toUiText
import com.nhuhuy.replee.feature_chat.R

@Composable
fun SetNickNameSheet(
    input: DynamicInput,
    onValueChange: (name: String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    SheetContainer(
        onDismiss = onDismiss
    ) {
        Text(
            text = stringResource(R.string.sheet_title_set_nick_name),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        NormalTextField(
            modifier = Modifier.fillMaxWidth(),
            label = R.string.sheet_title_text_field_label,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = null
                )
            },
            dynamicInput = input,
            onValueChange = onValueChange,
            errorText = input.validateResult.toUiText(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        CommonButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            res = R.string.sheet_title_set_nick_name_btn,
            enabled = input.valid,
            onClick = onConfirm
        )
    }
}