package com.notesapp.compressify.domain.useCase

import android.net.Uri
import android.os.Build
import android.util.Log
import com.iceteck.silicompressorr.SiliCompressor
import com.notesapp.compressify.CompressApplication
import com.notesapp.compressify.util.FileUtil
import java.io.File
import javax.inject.Inject

class CompressAndSaveVideoUseCase @Inject constructor(): BaseUseCase<CompressAndSaveVideoUseCase.Params, Unit>() {
    data class Params(
        val uris: List<Uri>,
    ) : Parameters()


    override suspend fun launch(parameters: Params) {
        parameters.uris.forEach{
            compressVideo(it)
        }
    }
    private fun compressVideo(uri: Uri){
        val filePath = SiliCompressor.with(CompressApplication.appContext)
            .compressVideo(uri, FileUtil.videoFilesDirectory.absolutePath)
        Log.d("VideoCompressorResult", "Compressed video saved to $filePath")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val file = File(filePath)
            val resultFolder = FileUtil.videoFilesDirectory
            file.copyTo(File(resultFolder, file.name), true)
        }
    }
}