package com.notesapp.compressify.domain.useCase

import android.net.Uri
import com.iceteck.silicompressorr.Util.getFilePath
import java.io.File
import javax.inject.Inject


class DeleteOriginalUseCase @Inject constructor(): BaseUseCase<DeleteOriginalUseCase.Parameters, Unit>() {

    data class Parameters(
        val filePath : String
    ) : BaseUseCase.Parameters()

    override suspend fun launch(parameters: Parameters) {

        val fileToDelete = File(parameters.filePath)
        if (fileToDelete.delete()) {
            println("file Deleted :")
        } else {
            println("file not Deleted :")
        }
    }
}
