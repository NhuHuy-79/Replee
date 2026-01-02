@file:OptIn(ExperimentalMaterial3Api::class)

package com.nhuhuy.replee.feature_profile.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Nightlight
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
import androidx.compose.ui.semantics.SemanticsProperties.Text
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.core.design_system.component.UserImage

@Preview
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier
){
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = "Settings",
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
                ProfileUserCard()
            }

            item {
                Text(
                    text = "General",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(vertical = 18.dp)
                        .padding(horizontal = 16.dp)
                )
            }

            item {
                ProfileItem(
                    onItemClick = {},
                    icon = Icons.Outlined.Notifications,
                    text = "Notification"
                )
            }

            item {
                ProfileItem(
                    onItemClick = {},
                    icon = Icons.Outlined.DarkMode,
                    text = "Appearance"
                )
            }

            item {
                ProfileItem(
                    onItemClick = {},
                    icon = Icons.Outlined.Lock,
                    text = "Privacy"
                )
            }

            item {
                ProfileItem(
                    onItemClick = {},
                    icon = Icons.Outlined.Cloud,
                    text = "Storage & Data"
                )
            }

            item {
                ProfileItem(
                    onItemClick = {},
                    icon = Icons.Outlined.Info,
                    text = "About"
                )
            }
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
    ProfileItem(
        modifier = Modifier.fillMaxWidth(),
        icon = Icons.Outlined.DarkMode,
        text = "Dark Mode",
        onItemClick = {}
    )

}