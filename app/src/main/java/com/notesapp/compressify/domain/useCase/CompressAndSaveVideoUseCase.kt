package com.notesapp.compressify.domain.useCase

import android.net.Uri
import android.util.Log
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import com.abedelazizshe.lightcompressorlibrary.config.SaveLocation
import com.abedelazizshe.lightcompressorlibrary.config.SharedStorageConfiguration
import com.notesapp.compressify.CompressApplication
import com.notesapp.compressify.domain.model.LibraryModel
import com.notesapp.compressify.domain.model.MediaCategory
import com.notesapp.compressify.service.VideoCompressionService
import com.notesapp.compressify.util.getVideoHeight
import com.notesapp.compressify.util.getVideoWidth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.math.roundToInt

class CompressAndSaveVideoUseCase @Inject constructor(private val addLibraryItemUseCase: AddLibraryItemUseCase) :
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

    private suspend fun compressVideo(
        compressionModel: VideoCompressionService.VideoCompressionModel,
        onProgress: (percent: Int) -> Unit = {},
        onComplete: () -> Unit = {}
    ) {
        val originalToCompressedUri = mutableMapOf<Uri, Uri>()

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
                    saveVideoToLibrary(listOf(compressionModel.uri), listOf(Uri.parse(path)))
                    onComplete()
                }
            }
        )
//        saveVideoToLibrary(listOf(compressionModel.uri), listOf(compressionModel.uri))
    }



    private  fun saveVideoToLibrary(uris : List<Uri>, compressedUris : List<Uri>) = CompressApplication.App.applicationScope.launch(Dispatchers.IO){
        val libraryModels = uris.zip(compressedUris).map {(uri, compressedUri)->
            LibraryModel(
                id = UUID.randomUUID().toString(),
                originalURI = uri,
                compressedURI = compressedUri,
                timeStamp = System.currentTimeMillis(),
                category = MediaCategory.VIDEO
            )
        }
        val parameters = AddLibraryItemUseCase.Parameters(libraryModels)
        addLibraryItemUseCase.launch(parameters)
    }

}