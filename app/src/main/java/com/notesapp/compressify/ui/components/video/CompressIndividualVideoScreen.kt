package com.notesapp.compressify.ui.components.video

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.notesapp.compressify.domain.model.ImageModel
import com.notesapp.compressify.domain.model.VideoModel
import com.notesapp.compressify.ui.components.home.common.CompressOptionsHeader
import com.notesapp.compressify.ui.components.home.common.PrimaryButton
import com.notesapp.compressify.ui.components.image.IndividualImageCompressedCard
import com.notesapp.compressify.ui.viewmodel.MainViewModel
import com.notesapp.compressify.util.UIEvent

@Composable
fun CompressIndividualVideoScreen(
    modifier : Modifier = Modifier,
    selectedVideos: List<VideoModel>,
    compressionOptions: MainViewModel.VideoCompressionOptions,
    onUIEvent: (UIEvent) -> Unit
) {
    val individualOptions = remember(compressionOptions) {
        mutableStateMapOf<Uri, MainViewModel.VideoCompressionOptions>().apply {
            putAll(selectedVideos.map { it.uri to compressionOptions })
        }
    }
    val selectedVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            onUIEvent(UIEvent.Videos.OnVideosAdded(uris))
        }
    )

    Column(modifier = modifier) {
        CompressOptionsHeader(
            modifier = Modifier.fillMaxWidth(),
            numberOfFiles = selectedVideos.size,
            filesSize = selectedVideos.sumOf { it.size }
        ) {
            selectedVideoLauncher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.VideoOnly
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (video in selectedVideos) {
                val options by derivedStateOf {
                    individualOptions[video.uri] ?: compressionOptions
                }
                item {
                    IndividualVideoCompressedCard(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillParentMaxWidth(0.9f),
                        video = video,
                        compressionOptions = options,
                        onCompressionOptionsChanged = {
                            individualOptions[video.uri] = it
                        },
                        onUIEvent = onUIEvent
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (selectedVideos.isNotEmpty()) {
            PrimaryButton(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                onClick = {
                    onUIEvent(
                        UIEvent.Videos.OnStartCompressionClick(
                            individualOptions.toList()
                        )
                    )
                },
                buttonText = "Start Compression"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}