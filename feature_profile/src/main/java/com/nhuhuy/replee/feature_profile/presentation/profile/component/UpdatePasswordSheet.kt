@file:OptIn(ExperimentalMaterial3Api::class)

package com.nhuhuy.replee.feature_profile.presentation.profile.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.core.design_system.component.CommonButton
import com.nhuhuy.replee.core.design_system.component.SecureTextField
import com.nhuhuy.replee.core.design_system.state.ScreenState
import com.nhuhuy.replee.core.design_system.state.ScreenStateHost
import com.nhuhuy.replee.feature_profile.R
import com.nhuhuy.replee.feature_profile.presentation.profile.state.ProfileState
import com.nhuhuy.replee.feature_profile.utils.toUiText

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UpdatePasswordSheet(
    state: ProfileState,
    result: ScreenState<Unit>,
    onOldPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
        modifier = modifier
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Icon(
                imageVector = Icons.Rounded.Lock,
                contentDescription = null,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.profile_password_edit_title),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            SecureTextField(
                modifier = Modifier.fillMaxWidth(),
                label = R.string.profile_old_passwd,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.LockOpen,
                        contentDescription = null
                    )
                },
                errorText = state.oldPassword.validateResult.toUiText(),
                validatableInput = state.oldPassword,
                onValueChange = onOldPasswordChange
            )

            SecureTextField(
                modifier = Modifier.fillMaxWidth(),
                label = R.string.profile_new_passwd,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Password,
                        contentDescription = null
                    )
                },
                errorText = state.newPassword.validateResult.toUiText(),
                validatableInput = state.newPassword,
                onValueChange = onNewPasswordChange
            )

            ScreenStateHost(
                state = result,
                modifier = Modifier.fillMaxWidth(),
                success = {
                    Text(
                        text = stringResource(R.string.update_password_success),
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                failure = {
                    Text(
                        text = stringResource(R.string.update_password_failed),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                },
                loading = {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )

            Spacer(Modifier.height(16.dp))

            CommonButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                res = R.string.profile_confirm_btn,
                enabled = state.valid,
                onClick = onConfirm
            )
        }
    }
}

@Preview
@Composable
fun SheetPreview(){
    UpdatePasswordSheet(
        state = ProfileState(),
        onOldPasswordChange = {},
        onNewPasswordChange = {},
        result = ScreenState.Idle,
        onConfirm = {},
        onDismiss = {}
    )
}