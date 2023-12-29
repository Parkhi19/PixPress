package com.notesapp.compressify.util

sealed interface UIEvent {
    sealed interface Images : UIEvent {

        data class RemoveImageClicked(val path: String) : Images
        data class CompressionOptionsConfirmed(
            val resolution: Float,
            val quality: Float,
            val keepOriginal: Boolean
        ) : Images
    }
}