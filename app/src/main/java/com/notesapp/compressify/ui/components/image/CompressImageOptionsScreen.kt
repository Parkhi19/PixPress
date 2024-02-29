package com.notesapp.compressify.ui.components.image

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
import com.notesapp.compressify.domain.model.ImageModel
import com.notesapp.compressify.domain.model.NavigationRoutes
import com.notesapp.compressify.ui.components.home.common.CompressOptionsFooter
import com.notesapp.compressify.ui.components.home.common.CompressOptionsHeader
import com.notesapp.compressify.ui.theme.primaryColor
import com.notesapp.compressify.ui.viewmodel.MainViewModel
import com.notesapp.compressify.util.UIEvent

@Composable
fun CompressImageOptionsScreen(
    modifier: Modifier = Modifier,
    compressImagesUIState: CompressImagesUIState,
    onUIEvent: (UIEvent) -> Unit
) {
    var resolution by remember {
        mutableFloatStateOf(MainViewModel.INITIAL_IMAGE_RESOLUTION)
    }
    var quality by remember {
        mutableFloatStateOf(MainViewModel.INITIAL_IMAGE_QUALITY)
    }
    var deleteOriginal by remember {
        mutableStateOf(false)
    }

    val (selectedImages, isImageProcessing) = compressImagesUIState
    var showOptionsBottomSheet by remember {
        mutableStateOf(false)
    }

    val selectedPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            onUIEvent(UIEvent.Images.OnImagesAdded(uris))
        }
    )
    if (isImageProcessing) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp), color = primaryColor)
        }
    }
    Column(modifier = modifier) {
        CompressOptionsHeader(
            modifier = Modifier.fillMaxWidth(),
            numberOfFiles = selectedImages.size,
            filesSize = selectedImages.sumOf { it.size }
        ) {
            selectedPhotoLauncher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.weight(1f)) {
            items(selectedImages.size) {
                ImagePreviewCard(
                    image = selectedImages[it],
                    modifier = Modifier.padding(8.dp),
                    onDeleteClick = { path ->
                        onUIEvent(UIEvent.Images.RemoveImageClicked(path))
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (selectedImages.isNotEmpty()) {
            CompressOptionsFooter(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                onOptionsClick = {
                    showOptionsBottomSheet = true
                },
                onContinueClick = {
                    onUIEvent(
                        UIEvent.Images.ImageCompressionOptionsApplied(
                            resolution = resolution,
                            quality = quality,
                            deleteOriginal = deleteOriginal
                        )
                    )
                    onUIEvent(UIEvent.Navigate(NavigationRoutes.INDIVIDUAL_IMAGE_PREVIEW))
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        if (showOptionsBottomSheet) {
            CompressionImageOptionsDialog(
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
data class CompressImagesUIState(
    val selectedImages: List<ImageModel> = emptyList(),
    val isImageProcessing: Boolean = false
)


