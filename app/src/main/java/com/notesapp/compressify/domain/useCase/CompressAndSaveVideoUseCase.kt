package com.notesapp.compressify.domain.useCase

import android.os.Build
import android.util.Log
import androidx.core.net.toFile
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import com.abedelazizshe.lightcompressorlibrary.config.SaveLocation
import com.abedelazizshe.lightcompressorlibrary.config.SharedStorageConfiguration
import com.iceteck.silicompressorr.SiliCompressor
import com.notesapp.compressify.CompressApplication
import com.notesapp.compressify.service.VideoCompressionService
import com.notesapp.compressify.util.FileUtil
import com.notesapp.compressify.util.getVideoHeight
import com.notesapp.compressify.util.getVideoWidth
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

class CompressAndSaveVideoUseCase @Inject constructor() :
    BaseUseCase<CompressAndSaveVideoUseCase.Params, Int>() {
    data class Params(
        val videosToOptions: List<VideoCompressionService.VideoCompressionModel>
    ) : Parameters()

    override suspend fun launch(parameters: CompressAndSaveVideoUseCase.Params): Int {
        coroutineScope {
            parameters.videosToOptions.forEach { compressionModel ->
//                compressVideo(compressionModel)
                compressTest(compressionModel)
            }
        }
        return parameters.videosToOptions.size
    }

    override fun launchWithFlow(parameters: Params): Flow<Int> = flow {
        emit(0)
        coroutineScope {
            var compressed = 0
            parameters.videosToOptions.forEach { compressionModel ->
                compressTest(compressionModel)
//                compressVideo(compressionModel)
                if (compressionModel.deleteOriginal) {
                    compressionModel.uri.toFile().delete()
                }
                compressed++
                emit(compressed)
            }
        }
    }

    private fun compressVideo(compressionModel: VideoCompressionService.VideoCompressionModel) {
        val height = compressionModel.uri.getVideoHeight() * compressionModel.resolution
        val width = compressionModel.uri.getVideoWidth() * compressionModel.resolution

        val filePath = SiliCompressor.with(CompressApplication.appContext)
            .compressVideo(
                compressionModel.uri,
                FileUtil.videoFilesDirectory.absolutePath,
                width.toInt(),
                height.toInt(),
                0
            )
        Log.d("VideoCompressorResult", "Compressed video saved to $filePath")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val file = File(filePath)
            val resultFolder = FileUtil.videoFilesDirectory
            file.copyTo(File(resultFolder, file.name), true)
        }
    }

    private fun compressTest(compressionModel: VideoCompressionService.VideoCompressionModel) {
        VideoCompressor.start(
            context = CompressApplication.appContext,
            uris = listOf(compressionModel.uri),
            isStreamable = true,
            sharedStorageConfiguration = SharedStorageConfiguration(
                saveAt = SaveLocation.movies,
            ),
            configureWith = Configuration(
                quality = VideoQuality.HIGH,
                videoNames = listOf(compressionModel.uri.pathSegments.last()),
                isMinBitrateCheckEnabled = false,
                videoBitrateInMbps = 5,
                disableAudio = false,
                keepOriginalResolution = false,
                videoWidth = 720.0,
                videoHeight = 1280.0,
            ),
            listener = object : CompressionListener {
                override fun onCancelled(index: Int) {
                    Log.d("onCancelled", index.toString())
                }

                override fun onFailure(index: Int, failureMessage: String) {
                    Log.d("onFailure", failureMessage)
                }

                override fun onProgress(index: Int, percent: Float) {
                    Log.d("onProgress", index.toString())
                }

                override fun onStart(index: Int) {
                    Log.d("onStart", index.toString())
                }

                override fun onSuccess(index: Int, size: Long, path: String?) {
                    Log.d("onSuccess", path.toString())
                }
            }
        )
    }
}