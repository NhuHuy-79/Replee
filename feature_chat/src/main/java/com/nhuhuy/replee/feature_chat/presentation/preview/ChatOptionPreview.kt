package com.nhuhuy.replee.feature_chat.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.nhuhuy.replee.core.design_system.component.PreviewFrame
import com.nhuhuy.replee.core.design_system.theme.RepleeTheme
import com.nhuhuy.replee.core.model.settings.SeedColor
import com.nhuhuy.replee.feature_chat.presentation.option.OptionScreen
import com.nhuhuy.replee.feature_chat.presentation.option.state.OptionState

@Preview
@Composable
fun ChatOptionPreview() {
    RepleeTheme(darkTheme = true) {
        PreviewFrame(title = "Chat Options") {
            OptionScreen(
                color = SeedColor.SAPPHIRE,
                state = OptionState(
                    otherUserName = "Alex Johnson",
                    otherUserEmail = "alex.j@example.com",
                    otherUserImg = "https://images.unsplash.com/photo-1599566150163-29194dcaad36?auto=format&fit=crop&w=250&q=80",
                    pinConversation = true,
                    muteConversation = false
                ),
                onAction = {}
            )
        }
    }
}
