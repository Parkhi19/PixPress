package com.notesapp.compressify.ui.components.video

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.notesapp.compressify.ui.components.home.common.CompressionOptionsSlider
import com.notesapp.compressify.ui.components.home.common.PrimaryButton
import com.notesapp.compressify.ui.theme.primaryColor
import com.notesapp.compressify.ui.theme.primaryTintedColor
import kotlinx.coroutines.launch


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CompressionVideoOptionsDialog(
    modifier: Modifier,
    onDismiss: () -> Unit,
    initialResolution: Float,
    initialQuality: VideoQuality,
    initialDeleteOriginal: Boolean,
    onConfirm: (Float, VideoQuality, Boolean) -> Unit
) {
    val videoQualityLevels = listOf("Very Low", "Low", "Medium", "High", "Very High")

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(onDismissRequest = { onDismiss() }, sheetState = sheetState) {
        val textColor = MaterialTheme.colorScheme.onBackground
        var resolution by mutableFloatStateOf(initialResolution)
        var quality by mutableStateOf(initialQuality)
        var deleteOriginal by mutableStateOf(initialDeleteOriginal)

        val videoQualitySliderToVideoQualityMap = listOf(
            0f to VideoQuality.VERY_LOW,
            1f to VideoQuality.LOW,
            2f to VideoQuality.MEDIUM,
            3F to VideoQuality.HIGH,
            4f to VideoQuality.VERY_HIGH
        )

        Card(
            shape = RoundedCornerShape(
                size = 8.dp
            ),
            modifier = modifier,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Compress Options",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = textColor
                    )
                }
                Spacer(
                    modifier = Modifier
                        .height(2.dp)
                        .fillMaxWidth()
                        .background(primaryTintedColor)
                        .padding(vertical = 12.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    CompressionOptionsSlider(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Select Resolution",
                        labelValue = "${(resolution * 100).toInt()} %",
                        value = resolution,
                    ) {
                        resolution = it
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Text(
                            text = "Quality",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp),
                            color = textColor
                        )
                        Text(
                            text = videoQualityLevels[videoQualitySliderToVideoQualityMap.indexOfFirst { it.second == quality }],
                            style = MaterialTheme.typography.bodySmall,
                            color = textColor
                        )
                    }
                    Slider(
                        value = videoQualitySliderToVideoQualityMap.first { it.second == quality }.first,
                        valueRange = 0f..4f,
                        colors = SliderDefaults.colors(
                            activeTrackColor = primaryColor,
                            thumbColor = primaryColor,
                            inactiveTrackColor = primaryColor.copy(alpha = 0.7f)
                        ),
                        steps = 3,
                        onValueChange = {videoQuality->
                            quality = videoQualitySliderToVideoQualityMap.first { it.first == videoQuality }.second
                        }
                    )
                }

//                Row(modifier = Modifier.fillMaxWidth()) {
//                    CompressionOptionsSlider(
//                        modifier = Modifier.fillMaxWidth(),
//                        label = "Select Quality",
//                        labelValue = "${(quality * 100).toInt()} %",
//                        value = quality,
//                    ) {
//                        quality = it
//                    }
//                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = deleteOriginal,
                            onCheckedChange = { deleteOriginal = it },
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Text(
                            text = "Delete Original",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp),
                            color = textColor
                        )
                    }

                    PrimaryButton(
                        onClick = {
                            onConfirm(resolution, quality, deleteOriginal)
                            coroutineScope.launch {
                                sheetState.hide()
                            }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    onDismiss()
                                }
                            }
                        },
                        buttonText = "Apply",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    )
                }
            }
        }
    }
}