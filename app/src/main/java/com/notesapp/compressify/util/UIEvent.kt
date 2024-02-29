package com.notesapp.compressify.util

import android.net.Uri
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.notesapp.compressify.domain.model.NavigationRoutes
import com.notesapp.compressify.ui.viewmodel.MainViewModel

sealed interface UIEvent {
    sealed interface Images : UIEvent {

        data class RemoveImageClicked(val path: String) : Images
        data class ImageCompressionOptionsApplied(
            val resolution: Float,
            val quality: Float,
            val deleteOriginal: Boolean
        ) : Images

        data class OnImagesAdded(val uris: List<Uri>) : Images

        data class OnStartCompressionClick(
            val imagesToOptions : List<Pair<Uri, MainViewModel.ImageCompressionOptions>>
        ): Images

    }

    sealed interface Videos : UIEvent{
        data class RemoveVideoClicked(val path: String) : Videos
        data class VideoCompressionOptionsApplied(
            val resolution: Float,
            val quality: VideoQuality,
            val deleteOriginal: Boolean
        ) : Videos

        data class OnVideosAdded(val uris: List<Uri>) : Videos

        data class OnStartCompressionClick(
            val videosToOptions : List<Pair<Uri, MainViewModel.VideoCompressionOptions>>
        ): Videos
    }
    data class Navigate(val route : NavigationRoutes) : UIEvent
}