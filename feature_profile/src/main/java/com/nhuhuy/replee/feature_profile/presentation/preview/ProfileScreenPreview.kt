package com.nhuhuy.replee.feature_profile.presentation.preview

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers.GREEN_DOMINATED_EXAMPLE
import com.nhuhuy.replee.core.design_system.component.PreviewFrame
import com.nhuhuy.replee.core.design_system.theme.RepleeTheme
import com.nhuhuy.replee.core.model.account.Account
import com.nhuhuy.replee.feature_profile.presentation.ProfileScreen
import com.nhuhuy.replee.feature_profile.presentation.profile.state.ProfileActionResult
import com.nhuhuy.replee.feature_profile.presentation.profile.state.ProfileState

@Preview(
    wallpaper = GREEN_DOMINATED_EXAMPLE
)
@Composable
fun ProfileScreenPreview() {
    RepleeTheme(
        darkTheme = true,
        dynamicColor = true
    ) {
        PreviewFrame(
            title = "Make it yours",
            backgroundColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            ProfileScreen(
                profileActionResult = ProfileActionResult(),
                state = fakeProfileState,
                onAction = {}
            )
        }
    }
}

val fakeProfileState = ProfileState(
    account = Account(
        id = "me_123",
        name = "Nhu Huy",
        email = "nhuhuy@example.com",
        imageUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=250&q=80"
    )
)
