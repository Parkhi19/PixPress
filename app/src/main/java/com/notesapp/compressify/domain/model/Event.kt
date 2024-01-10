package com.notesapp.compressify.domain.model

sealed interface Event {
    object CompressionCompleted : Event
    object Compressing : Event
}