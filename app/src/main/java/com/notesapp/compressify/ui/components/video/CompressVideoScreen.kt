package com.notesapp.compressify.ui.components.video

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.notesapp.compressify.domain.model.VideoModel
import com.notesapp.compressify.util.UIEvent

@Composable
fun CompressVideoScreen(
    selectedVideos: List<VideoModel>,
    onVideoSelectClick: () -> Unit,
    onUIEvent: (UIEvent) -> Unit,
    modifier: Modifier = Modifier,
    isVideoProcessing: Boolean = false,
) {
    if(selectedVideos.isEmpty()) {
        SelectVideoScreen(onVideoSelect = onVideoSelectClick, modifier = Modifier, isVideoProcessing = isVideoProcessing)
    } else {

    }
}