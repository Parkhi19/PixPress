package com.notesapp.compressify.data.repository

import com.notesapp.compressify.data.entities.LibraryEntity
import com.notesapp.compressify.domain.model.LibraryModel
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {
    fun getLibraryItems() : Flow<List<LibraryModel>>
    suspend fun addLibraryItems(libraryModels : List<LibraryModel>)
    suspend fun deleteLibraryItems(ids : List<String>)
}