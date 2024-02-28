package com.notesapp.compressify.ui.components.video

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.notesapp.compressify.domain.model.VideoModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.notesapp.compressify.ui.components.home.common.CompressionOptionsSlider
import com.notesapp.compressify.ui.theme.primaryColor
import com.notesapp.compressify.ui.theme.primaryTintedColor
import com.notesapp.compressify.ui.viewmodel.MainViewModel
import com.notesapp.compressify.util.UIEvent
import com.notesapp.compressify.util.createImageThumbnail
import com.notesapp.compressify.util.createVideoThumbnail
import com.notesapp.compressify.util.getFormattedSize
import javax.annotation.meta.When

@Composable
fun IndividualVideoCompressedCard(
    modifier: Modifier = Modifier,
    video: VideoModel,
    compressionOptions: MainViewModel.VideoCompressionOptions,
    onCompressionOptionsChanged: (MainViewModel.VideoCompressionOptions) -> Unit,
    onUIEvent: (UIEvent) -> Unit

) {
    val (resolution, quality, deleteOriginal) = compressionOptions

    val videoQualityLevels = listOf("Very Low", "Low", "Medium", "High", "Very High")
    val videoQualitySliderToVideoQualityMap = listOf(
        0f to VideoQuality.VERY_LOW,
        1f to VideoQuality.LOW,
        2f to VideoQuality.MEDIUM,
        3F to VideoQuality.HIGH,
        4f to VideoQuality.VERY_HIGH
    )

    ElevatedCard(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                val (detailsRef, imageRef) = createRefs()
                Image(
                    bitmap = video.uri.createVideoThumbnail().asImageBitmap(),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.4f)
                        .constrainAs(imageRef) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color(0xD9000000))
                        .constrainAs(detailsRef) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                ) {
                    Text(
                        text = video.name,
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(start = 8.dp, end = 4.dp),
                        color = Color.White,
                        overflow = TextOverflow.Ellipsis
                    )
                    IconButton(onClick = {
                        onUIEvent(UIEvent.Videos.RemoveVideoClicked(video.uri.path.toString()))
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "",
                            modifier = Modifier.size(24.dp),
                            tint = Color.White
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Original Size: ${video.size.getFormattedSize()}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Original Resolution : ${video.width} x ${video.height}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Original Bitrate : ${video.bitrate} kbps")
            }
            Spacer(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth()
                    .background(color = primaryTintedColor)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CompressionOptionsSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    label = "Resolution",
                    labelValue = video.width.times(resolution).toInt()
                        .toString() + " x " + video.height.times(resolution).toInt().toString(),
                    value = resolution,
                    onValueChange = {
                        onCompressionOptionsChanged(
                            compressionOptions.copy(
                                resolution = it
                            )
                        )
                    }
                )

                val textColor = MaterialTheme.colorScheme.onBackground
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
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
                            text = videoQualityLevels[videoQualitySliderToVideoQualityMap.indexOfFirst {
                                it.second == quality
                            }],
                            style = MaterialTheme.typography.bodySmall,
                            color = textColor
                        )
                    }
                    Slider(
                        value = videoQualitySliderToVideoQualityMap.first {
                            it.second == quality
                        }.first,

                        valueRange = 0f..4f,
                        colors = SliderDefaults.colors(
                            activeTrackColor = primaryColor,
                            thumbColor = primaryColor,
                            inactiveTrackColor = primaryColor.copy(alpha = 0.7f)
                        ),
                        steps = 3,
                        onValueChange = {videoQuality->
                            onCompressionOptionsChanged(
                                compressionOptions.copy(
                                    quality = videoQualitySliderToVideoQualityMap.first { it.first == videoQuality }.second
                                )
                            )
                        }
                    )
                }


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = deleteOriginal,
                        onCheckedChange = {
                            onCompressionOptionsChanged(
                                compressionOptions.copy(
                                    deleteOriginal = it
                                )
                            )
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Text(
                        text = "Delete Original",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}