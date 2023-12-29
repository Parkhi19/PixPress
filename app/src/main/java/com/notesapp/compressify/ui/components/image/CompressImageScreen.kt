package com.notesapp.compressify.ui.components.image

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.notesapp.compressify.domain.model.ImageModel
import com.notesapp.compressify.util.UIEvent

@Composable
fun CompressImageScreen(
    selectedImages: List<ImageModel>,
    onImageSelectClick: () -> Unit,
    onUIEvent: (UIEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    if (selectedImages.isEmpty()) {
        SelectImagesScreen(onImageSelect = onImageSelectClick, modifier = Modifier.fillMaxSize())
    } else {
        CompressOptionsScreen(
            selectedImages = selectedImages,
            modifier = Modifier.fillMaxSize(),
            onUIEvent = onUIEvent
        )
    }
}