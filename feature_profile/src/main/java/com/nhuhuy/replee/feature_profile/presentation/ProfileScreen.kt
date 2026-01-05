@file:OptIn(ExperimentalMaterial3Api::class)

package com.nhuhuy.replee.feature_profile.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.core.design_system.component.BoxContainer
import com.nhuhuy.replee.feature_profile.R
import com.nhuhuy.replee.feature_profile.presentation.profile.component.ProfileUserCard
import com.nhuhuy.replee.feature_profile.presentation.profile.component.NotificationDialog
import com.nhuhuy.replee.feature_profile.presentation.profile.component.SelectThemeDialog
import com.nhuhuy.replee.feature_profile.presentation.profile.state.Overlay
import com.nhuhuy.replee.feature_profile.presentation.profile.state.ProfileAction
import com.nhuhuy.replee.feature_profile.presentation.profile.state.ProfileState
import com.nhuhuy.replee.feature_profile.presentation.profile.component.UpdateAccountSheet

@Composable
fun ProfileScreen(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit
)= BoxContainer {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.profile_screen_title),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                ProfileUserCard(
                    user = state.account,
                    onEditClick = {
                        onAction(ProfileAction.OnUpdatePassword.BottomSheet)
                    }
                )
            }

            item {
                Text(
                    text = stringResource(R.string.profile_screen_subtitle),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(vertical = 18.dp)
                        .padding(horizontal = 16.dp)
                )
            }

            item {
                ProfileItem(
                    onItemClick = {
                        onAction(ProfileAction.OnNotificationClick.Dialog)
                    },
                    icon = Icons.Outlined.Notifications,
                    text = stringResource(R.string.profile_item_notification)
                )
            }

            item {
                ProfileItem(
                    onItemClick = {
                        onAction(ProfileAction.OnDarkModeClick.Dialog)
                    },
                    icon = Icons.Outlined.DarkMode,
                    text = stringResource(R.string.profile_item_theme)
                )
            }

            item {
                ProfileItem(
                    onItemClick = {
                        //TODO("open password edit)
                    },
                    icon = Icons.Outlined.Lock,
                    text = "Privacy"
                )
            }

            item {
                ProfileItem(
                    onItemClick = {
                        //TODO("storage ??/)
                    },
                    icon = Icons.Outlined.Cloud,
                    text = stringResource(R.string.profile_item_storage)
                )
            }

            item {
                ProfileItem(
                    onItemClick = {
                        onAction(ProfileAction.OnLogOut)
                    },
                    icon = Icons.AutoMirrored.Outlined.Logout,
                    text = stringResource(R.string.profile_item_logout)
                )
            }

            item {
                ProfileItem(
                    onItemClick = {
                        onAction(ProfileAction.OnAboutClick)
                    },
                    icon = Icons.Outlined.Info,
                    text = stringResource(R.string.profile_item_about)
                )
            }
        }

        when (state.overlay) {
            Overlay.NONE -> Unit
            Overlay.THEME -> SelectThemeDialog(
                modifier = Modifier,
                currentThemeMode = state.darkMode,
                onThemeModeSelect = { theme ->
                    onAction(ProfileAction.OnDarkModeClick.Select(theme))
                },
                onDismiss = {
                    onAction(ProfileAction.OnDismiss)
                }
            )
            Overlay.NOTIFICATION -> NotificationDialog(
                currentOption = state.notification,
                onNotificationSelect = { notification ->
                    onAction(ProfileAction.OnNotificationClick.Select(notification))
                },
                onDismiss = {
                    onAction(ProfileAction.OnDismiss)
                },
            )
            Overlay.UPDATE_PASSWORD -> UpdateAccountSheet(
                state = state,
                onOldPasswordChange = { value ->
                    onAction(ProfileAction.OnOldPasswordChange(value))
                },
                onNewPasswordChange = { value ->
                    onAction(ProfileAction.OnNewPasswordChange(value))

                },
                onConfirm = {
                    onAction(ProfileAction.OnUpdatePassword.Confirm)
                },
                onDismiss = {
                    onAction(ProfileAction.OnDismiss)
                },
            )
        }
    }
}

@Composable
fun ProfileItem(
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit,
    icon: ImageVector,
    text: String
){
    ListItem(
        colors = ListItemDefaults.colors(
            headlineColor = MaterialTheme.colorScheme.secondary,
            leadingIconColor = MaterialTheme.colorScheme.secondary
        ),
        modifier = modifier.clickable{
            onItemClick()
        },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        },
        headlineContent = {
            Text(
                text = text
            )
        },
    )
}

@Preview
@Composable
fun ProfilePreview(){
    ProfileScreen(
        state = ProfileState(),
        onAction = {}
    )

}