package com.notesapp.compressify.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Parcelable
import android.util.Log
import androidx.core.app.NotificationCompat
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.notesapp.compressify.R
import com.notesapp.compressify.data.repository.LibraryRepository
import com.notesapp.compressify.domain.repository.LibraryRepositoryImpl
import com.notesapp.compressify.domain.useCase.AddLibraryItemUseCase
import com.notesapp.compressify.domain.useCase.CompressAndSaveImagesUseCase
import com.notesapp.compressify.domain.useCase.CompressAndSaveVideoUseCase
import com.notesapp.compressify.ui.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class VideoCompressionService : Service() {
    private val libraryRepository: LibraryRepository = LibraryRepositoryImpl()
    private val binder = VideoCompressionBinder()
    private val addLibraryItemUseCase = AddLibraryItemUseCase(libraryRepository)
    private val compressAndSaveVideoUseCase = CompressAndSaveVideoUseCase(addLibraryItemUseCase)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: ")
        val videosToOptions = intent?.getParcelableArrayListExtra(
            VIDEO_TO_OPTIONS
        ) ?: emptyList<VideoCompressionModel>()
        createNotificationChannel()
        val notificationBuilder =
            NotificationCompat.Builder(this, VIDEO_COMPRESSION_NOTIFICATION_ID)
                .setContentTitle("Compressing ${videosToOptions.size} Videos")
                .setProgress(100, 0, false)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.drawable.ic_category_video)
        val notification = notificationBuilder.build()
        startForeground(SERVICE_ID, notification)


        CoroutineScope(Dispatchers.IO).launch {
            val onProgress: (Int, Int) -> Unit = { compressed, totalProgress ->
                notificationBuilder.setContentTitle("Compressing ${videosToOptions.size} Videos (${totalProgress}%)")
                    .setProgress(100, totalProgress, false)
                    .setContentText("Compressed $compressed of ${videosToOptions.size} videos")
                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(SERVICE_ID, notificationBuilder.build())
                if (compressed >= videosToOptions.size) {
                    onComplete(compressed)
                }
            }
            compressAndSaveVideoUseCase.launch(
                CompressAndSaveVideoUseCase.Params(
                    videosToOptions = videosToOptions,
                    onProgress = onProgress
                )
            )
        }
        return START_STICKY
    }

    private fun onComplete(totalSize : Int){
        stopForeground(STOP_FOREGROUND_REMOVE)
        val completionNotification = NotificationUtil.createCompletionNotification(
            this@VideoCompressionService,
            "$totalSize Videos compressed"
        )
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(2, completionNotification)
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                VIDEO_COMPRESSION_NOTIFICATION_ID, VIDEO_COMPRESSION_NOTIFICATION_ID,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    @Parcelize
    data class VideoCompressionModel(
        val uri: Uri,
        val resolution: Float,
        val quality: VideoQuality,
        val deleteOriginal: Boolean
    ) : Parcelable {
    }

    inner class VideoCompressionBinder: Binder() {
        fun getService(): VideoCompressionService = this@VideoCompressionService
    }

    companion object {
        const val TAG = "VideoCompressionService"
        private const val VIDEO_TO_OPTIONS = "com.notesapp.compressify.service.videosToOptions"
        private const val VIDEO_COMPRESSION_NOTIFICATION_ID = "Video Compression"
        private const val SERVICE_ID = 101
        fun getIntent(
            context: Context,
            videosToOptions: List<Pair<Uri, MainViewModel.VideoCompressionOptions>>
        ) = Intent(context, VideoCompressionService::class.java).apply {
            val videoCompressionModels = videosToOptions.map {
                VideoCompressionModel(
                    uri = it.first,
                    resolution = it.second.resolution,
                    quality = it.second.quality,
                    deleteOriginal = it.second.deleteOriginal
                )
            }
            putParcelableArrayListExtra(
                VIDEO_TO_OPTIONS,
                ArrayList(videoCompressionModels)
            )
        }
    }
}