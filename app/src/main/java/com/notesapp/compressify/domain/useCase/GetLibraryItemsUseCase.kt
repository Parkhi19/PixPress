package com.notesapp.compressify.domain.useCase

import com.notesapp.compressify.data.repository.LibraryRepository
import com.notesapp.compressify.domain.model.LibraryModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class GetLibraryItemsUseCase @Inject constructor(
    private val libraryRepository: LibraryRepository
) : BaseUseCase<BaseUseCase.Parameters, List<LibraryModel>>(){

    override suspend fun launch(parameters: Parameters): List<LibraryModel> {
        return libraryRepository.getLibraryItems().firstOrNull() ?: emptyList()
    }

    override fun launchWithFlow(parameters: Parameters): Flow<List<LibraryModel>> {
        return libraryRepository.getLibraryItems()
    }
}