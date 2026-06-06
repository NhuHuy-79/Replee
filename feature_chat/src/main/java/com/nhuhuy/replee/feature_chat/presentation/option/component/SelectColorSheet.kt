package com.nhuhuy.replee.feature_chat.presentation.option.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.core.design_system.component.SheetContainer
import com.nhuhuy.replee.core.model.settings.SeedColor
import com.nhuhuy.replee.feature_chat.R
import com.nhuhuy.replee.feature_chat.utils.toDrawable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectColorSheet(
    onDismiss: () -> Unit = {},
    currentColor: SeedColor,
    onColorSelected: (SeedColor) -> Unit = {},
    colorList: List<SeedColor> = SeedColor.entries,
) {
    var pendingColor by remember { mutableStateOf(currentColor) }
    val carouselState = rememberCarouselState(
        initialItem = colorList.indexOf(currentColor),
        itemCount = { colorList.size }
    )

    SheetContainer(
        onDismiss = onDismiss
    ) {
        Text(
            text = stringResource(R.string.setting_choose_theme),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        HorizontalMultiBrowseCarousel(
            state = carouselState,
            preferredItemWidth = 100.dp,
            itemSpacing = 12.dp,
            contentPadding = PaddingValues(horizontal = 24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) { index ->
            val color = colorList[index]
            ThemePreviewItem(
                drawableRes = color.toDrawable(),
                isSelected = pendingColor == color,
                modifier = Modifier
                    .maskClip(RoundedCornerShape(16.dp))
                    .clickable { pendingColor = color }
            )
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                onColorSelected(pendingColor)
                onDismiss()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Save",
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        Spacer(Modifier.height(16.dp))
    }
}


@Composable
fun ThemePreviewItem(
    drawableRes: Int,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = drawableRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
