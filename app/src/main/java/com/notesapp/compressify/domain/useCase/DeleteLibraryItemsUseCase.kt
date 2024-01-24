package com.notesapp.compressify.domain.useCase

import com.notesapp.compressify.data.repository.LibraryRepository
import javax.inject.Inject

class DeleteLibraryItemsUseCase @Inject constructor(
    private val libraryRepository: LibraryRepository
) : BaseUseCase<DeleteLibraryItemsUseCase.Parameters, Unit>() {

    data class Parameters(
        val ids: List<String>
    ) : BaseUseCase.Parameters()

    override suspend fun launch(parameters: Parameters) {
        libraryRepository.deleteLibraryItems(parameters.ids)
    }
}