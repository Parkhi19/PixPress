package com.notesapp.compressify.domain.useCase

import android.net.Uri
import com.notesapp.compressify.data.repository.LibraryRepository
import javax.inject.Inject

class DeleteLibraryItemsUseCase @Inject constructor(
    private val libraryRepository: LibraryRepository
) : BaseUseCase<DeleteLibraryItemsUseCase.Parameters, Unit>() {

    data class Parameters(
        val uris : List<Uri>
    ) : BaseUseCase.Parameters()

    override suspend fun launch(parameters: Parameters) {
        libraryRepository.deleteLibraryItems(parameters.uris)
    }
}