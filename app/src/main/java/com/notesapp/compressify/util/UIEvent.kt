package com.notesapp.compressify.util

import android.net.Uri
import com.notesapp.compressify.domain.model.NavigationRoutes

sealed interface UIEvent {
    sealed interface Images : UIEvent {

        data class RemoveImageClicked(val path: String) : Images
        data class CompressionOptionsConfirmed(
            val resolution: Float,
            val quality: Float,
            val keepOriginal: Boolean
        ) : Images

        data class OnImagesAdded(val uris: List<Uri>) : Images
    }
    data class Navigate(val route : NavigationRoutes) : UIEvent
}