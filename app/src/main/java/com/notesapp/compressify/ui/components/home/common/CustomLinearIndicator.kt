package com.notesapp.compressify.ui.components.home.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notesapp.compressify.ui.theme.primaryColor
import com.notesapp.compressify.ui.theme.primaryTintedColor

@Composable
fun CustomLinearIndicator(
    modifier: Modifier = Modifier,
    startingPoint: Float = 0.0f,
    endPoint: Float = 0.0f
) {
    val progress = maxOf(endPoint - startingPoint, 0f) / 100
    val startingWidth = maxOf(0.0f, startingPoint) / 100
    val endWidth = minOf(100.0f, 100 - endPoint) / 100
    Row(
        modifier = modifier
            .background(color = primaryTintedColor, shape = RoundedCornerShape(50.dp))

    ) {
        if(startingWidth > 0) {
            Spacer(
                modifier = Modifier
                    .weight(startingWidth)
                    .fillMaxHeight()
            )
        }
        if(progress > 0) {
            Spacer(
                modifier = Modifier
                    .weight(progress)
                    .fillMaxHeight()
                    .background(color = primaryColor, shape = RoundedCornerShape(50.dp))
            )
        }
        if(endWidth > 0) {
            Spacer(
                modifier = Modifier
                    .weight(endWidth)
                    .fillMaxHeight()
            )
        }
    }
}

@Preview
@Composable
fun PrevCustom() {
    CustomLinearIndicator(modifier = Modifier
        .fillMaxWidth()
        .height(8.dp), startingPoint = 10f, endPoint = 90f)

}
