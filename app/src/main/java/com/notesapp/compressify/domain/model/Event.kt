package com.notesapp.compressify.domain.model

sealed interface Event {
    data class PopBackStackTo(val destination: NavigationRoutes) : Event
    data class ShowToast(val message: String) : Event
}