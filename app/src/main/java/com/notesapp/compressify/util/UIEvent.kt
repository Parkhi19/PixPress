package com.notesapp.compressify.util

import android.net.Uri
import com.notesapp.compressify.domain.model.NavigationRoutes

sealed interface UIEvent {
    sealed interface Images : UIEvent {

        data class RemoveImageClicked(val path: String) : Images
        data class ImageCompressionOptionsApplied(
            val resolution: Float,
            val quality: Float,
            val deleteOriginal: Boolean
        ) : Images

        data class OnImagesAdded(val uris: List<Uri>) : Images

    }

    sealed interface Videos : UIEvent{
        data class RemoveVideoClicked(val path: String) : Videos
        data class VideoCompressionOptionsApplied(
            val resolution: Float,
            val quality: Float,
            val deleteOriginal: Boolean
        ) : Videos

        data class OnVideosAdded(val uris: List<Uri>) : Videos
    }
    data class Navigate(val route : NavigationRoutes) : UIEvent
}