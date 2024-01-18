package com.notesapp.compressify.domain.useCase

import android.content.Context
import android.net.Uri
import javax.inject.Inject

class CompressAndSaveVideoUseCase @Inject constructor(): BaseUseCase<CompressAndSaveImagesUseCase.Params>() {
    data class Params(
        val context: Context,
        val uris: List<Uri>,
        val resolution : Float,
        val quality : Float,
        val keepOriginal : Boolean
    ) : Parameters()


    override suspend fun launch(parameters: CompressAndSaveImagesUseCase.Params) {

    }
}