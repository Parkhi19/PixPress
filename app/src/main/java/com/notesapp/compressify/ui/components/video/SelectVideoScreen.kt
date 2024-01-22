package com.notesapp.compressify.ui.components.video

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.notesapp.compressify.ui.components.home.common.PrimaryButton

@Composable
fun SelectVideoScreen(
    onVideoSelect: () -> Unit,
    modifier: Modifier = Modifier,
    isVideoProcessing: Boolean = false
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isVideoProcessing) {

        } else {
            PrimaryButton(
                onClick = onVideoSelect,
                buttonText = "Select Video to Compress",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }

}