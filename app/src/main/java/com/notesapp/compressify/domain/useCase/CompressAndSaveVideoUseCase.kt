package com.notesapp.compressify.domain.useCase

import android.util.Log
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import com.abedelazizshe.lightcompressorlibrary.config.SaveLocation
import com.abedelazizshe.lightcompressorlibrary.config.SharedStorageConfiguration
import com.notesapp.compressify.CompressApplication
import com.notesapp.compressify.service.VideoCompressionService
import com.notesapp.compressify.util.getVideoHeight
import com.notesapp.compressify.util.getVideoWidth
import javax.inject.Inject
import kotlin.math.roundToInt

class CompressAndSaveVideoUseCase @Inject constructor() :
    BaseUseCase<CompressAndSaveVideoUseCase.Params, Int>() {
    data class Params(
        val videosToOptions: List<VideoCompressionService.VideoCompressionModel>,
        val onProgress: (compressed: Int, totalProgress: Int) -> Unit
    ) : Parameters()

    override suspend fun launch(parameters: Params): Int {
        var compressed = 0
        val totalProgress = parameters.videosToOptions.associate {
            it.uri to 0
        }.toMutableMap()
        parameters.videosToOptions.forEach { compressionModel ->
            compressVideo(
                compressionModel = compressionModel,
                onProgress = {
                    totalProgress[compressionModel.uri] = it
                    parameters.onProgress(compressed, totalProgress.values.sum()/totalProgress.size)
                },
                onComplete = {
                    compressed++
                    totalProgress[compressionModel.uri] = 100
                    parameters.onProgress(compressed, totalProgress.values.sum()/totalProgress.size)
                }
            )
        }
        return parameters.videosToOptions.size
    }

    private fun compressVideo(
        compressionModel: VideoCompressionService.VideoCompressionModel,
        onProgress: (percent: Int) -> Unit = {},
        onComplete: () -> Unit = {}
    ) {
        val (width, height) = compressionModel.run {
            (uri.getVideoWidth() * resolution) to (uri.getVideoHeight() * resolution)
        }
        VideoCompressor.start(
            context = CompressApplication.appContext,
            uris = listOf(compressionModel.uri),
            isStreamable = true,
            sharedStorageConfiguration = SharedStorageConfiguration(
                saveAt = SaveLocation.movies,
                subFolderName = "PixPress"
            ),
            configureWith = Configuration(
                quality = VideoQuality.HIGH,
                videoNames = listOf(compressionModel.uri.pathSegments.last()),
                isMinBitrateCheckEnabled = false,
                disableAudio = false,
                keepOriginalResolution = false,
                videoWidth = width.toDouble(),
                videoHeight = height.toDouble()
            ),
            listener = object : CompressionListener {
                override fun onCancelled(index: Int) {
                    Log.d("onCancelled", index.toString())
                }

                override fun onFailure(index: Int, failureMessage: String) {
                    Log.d("onFailure", failureMessage)
                }

                override fun onProgress(index: Int, percent: Float) {
                    Log.d("onProgress", percent.toString())
                    onProgress(percent.roundToInt())
                }

                override fun onStart(index: Int) {
                    Log.d("onStart", index.toString())
                }

                override fun onSuccess(index: Int, size: Long, path: String?) {
                    onComplete()
                }
            }
        )
    }
}