package com.notesapp.compressify.ui.components.image

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.notesapp.compressify.ui.components.common.PrimaryButton

@Composable
fun SelectImagesScreen(onImageSelect: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        PrimaryButton(
            onClick = onImageSelect,
            buttonText = "Select Images to Compress",
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )


    }

}