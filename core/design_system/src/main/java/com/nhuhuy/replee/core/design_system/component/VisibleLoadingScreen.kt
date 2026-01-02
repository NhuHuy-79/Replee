package com.nhuhuy.replee.core.design_system.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.core.design_system.R

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
){
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(R.drawable.bg_loading),
            contentDescription = null,
            modifier = Modifier.size(250.dp)
        )

        Spacer(Modifier.height(8.dp))

        LinearProgressIndicator(
            modifier = Modifier.width(250.dp)
        )
    }
}
@Composable
fun VisibleLoadingScreen(
    modifier: Modifier,
    show: Boolean
){
    AnimatedVisibility(
        visible = show,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.surface),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(R.drawable.bg_loading),
                contentDescription = null,
                modifier = Modifier.size(250.dp)
            )

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                modifier = Modifier.width(250.dp)
            )
        }
    }
}

@Preview
@Composable
private fun LoadingScreenPreview() {
    VisibleLoadingScreen(
        modifier = Modifier.fillMaxSize(),
        show = true
    )
}