package com.notesapp.compressify.ui.components.video

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.notesapp.compressify.domain.model.NavigationRoutes
import com.notesapp.compressify.domain.model.VideoModel
import com.notesapp.compressify.ui.components.home.common.CompressOptionsFooter
import com.notesapp.compressify.ui.components.home.common.CompressOptionsHeader
import com.notesapp.compressify.ui.components.image.CompressionImageOptionsDialog
import com.notesapp.compressify.ui.theme.primaryColor
import com.notesapp.compressify.ui.viewmodel.MainViewModel
import com.notesapp.compressify.util.UIEvent

@Composable
fun CompressVideoOptionsScreen(
    modifier: Modifier = Modifier,
    compressVideosUIState: CompressVideosUIState,
    onUIEvent: (UIEvent) -> Unit
) {
    var resolution by remember {
        mutableFloatStateOf(MainViewModel.INITIAL_IMAGE_RESOLUTION)
    }

    var quality by remember {
        mutableStateOf(MainViewModel.INITIAL_VIDEO_QUALITY)
    }

    var deleteOriginal by remember {
        mutableStateOf(false)
    }

    var showOptionsBottomSheet by remember {
        mutableStateOf(false)
    }
    val (selectedVideos, isVideoProcessing) = compressVideosUIState
    val selectedVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { onUIEvent(UIEvent.Videos.OnVideosAdded(it)) }
    )
    if (isVideoProcessing) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp), color = primaryColor)
        }
    }
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
        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.weight(1f)) {
            items(selectedVideos.size) {
                VideoPreviewCard(
                    video = selectedVideos[it],
                    modifier = Modifier.padding(8.dp),
                    onDeleteClick = {
                        onUIEvent(UIEvent.Videos.RemoveVideoClicked(it))
                    })
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (selectedVideos.isNotEmpty()) {
            CompressOptionsFooter(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                onOptionsClick = {
                    showOptionsBottomSheet = true
                },
                onContinueClick = {
                    onUIEvent(
                    UIEvent.Videos.VideoCompressionOptionsApplied(
                        resolution = resolution,
                        quality = quality,
                        deleteOriginal = deleteOriginal
                    )
                    )
                    onUIEvent(UIEvent.Navigate(NavigationRoutes.INDIVIDUAL_VIDEO_PREVIEW))
                }

            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        if (showOptionsBottomSheet) {
            CompressionVideoOptionsDialog(
                modifier = Modifier.fillMaxWidth(),
                onDismiss = {
                    showOptionsBottomSheet = false
                },
                initialResolution = resolution,
                initialQuality = quality,
                initialDeleteOriginal = deleteOriginal,
                onConfirm = { appliedResolution, appliedQuality, appliedDeleteOriginal ->
                    resolution = appliedResolution
                    quality = appliedQuality
                    deleteOriginal = appliedDeleteOriginal
                }
            )
        }
    }
}

@Stable
data class CompressVideosUIState(
    val selectedVideos: List<VideoModel> = emptyList(),
    val isVideoProcessing: Boolean = false
)

