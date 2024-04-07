package com.notesapp.compressify.data.repository

import android.net.Uri
import com.notesapp.compressify.domain.model.LibraryModel
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {
    fun getLibraryItems() : Flow<List<LibraryModel>>
    suspend fun addLibraryItems(libraryModels : List<LibraryModel>)
    suspend fun deleteLibraryItems(uris : List<Uri>)
}