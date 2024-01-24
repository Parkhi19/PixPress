package com.notesapp.compressify.domain.useCase

import com.notesapp.compressify.data.repository.LibraryRepository
import com.notesapp.compressify.domain.model.LibraryModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AddLibraryItemUseCase @Inject constructor(
    private val libraryRepository: LibraryRepository
) : BaseUseCase<AddLibraryItemUseCase.Parameters, Unit>() {

    data class Parameters(
        val libraryModels: List<LibraryModel>
    ) : BaseUseCase.Parameters()

    override suspend fun launch(parameters: Parameters) {
        libraryRepository.addLibraryItems(parameters.libraryModels)
    }
}