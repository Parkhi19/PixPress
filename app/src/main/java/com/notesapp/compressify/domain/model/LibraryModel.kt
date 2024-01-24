package com.notesapp.compressify.domain.model

import android.net.Uri
import com.notesapp.compressify.data.entities.LibraryEntity

data class LibraryModel(
    val id : String? = null,
    val originalURI : Uri? = null,
    val compressedURI : Uri? = null,
    val timeStamp : Long = System.currentTimeMillis()
) {
    constructor(
        libraryEntity: LibraryEntity
    ): this(
        id = libraryEntity.id,
        libraryEntity.originalURI?.let { Uri.parse(it) },
        libraryEntity.compressedURI?.let { Uri.parse(it) },
        libraryEntity.timeStamp
    )
}