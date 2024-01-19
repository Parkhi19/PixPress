package com.notesapp.compressify.ui.components.image

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.notesapp.compressify.R
import com.notesapp.compressify.ui.components.common.PrimaryButton

@Composable
fun SelectImagesScreen(onImageSelect: () -> Unit, modifier: Modifier = Modifier, isImageProcessing : Boolean = false){
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.image_compressing))
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isImageProcessing) {
            LottieAnimation(
                modifier = modifier,
                composition = composition,
                iterations = LottieConstants.IterateForever,
            )
        } else {
            PrimaryButton(
                onClick = onImageSelect,
                buttonText = "Select Images to Compress",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}