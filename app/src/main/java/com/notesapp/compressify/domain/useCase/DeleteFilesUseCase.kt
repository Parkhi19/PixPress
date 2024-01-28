package com.notesapp.compressify.domain.useCase

import android.net.Uri
import androidx.core.net.toFile
import java.io.File
import javax.inject.Inject


class DeleteFilesUseCase @Inject constructor() :
    BaseUseCase<DeleteFilesUseCase.Parameters, Unit>() {

    data class Parameters(
        val filePaths: List<Uri>
    ) : BaseUseCase.Parameters()

    override suspend fun launch(parameters: Parameters) {
        parameters.filePaths.forEach {
            it.toFile().delete()
        }
    }
}
