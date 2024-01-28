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
import androidx.core.app.NotificationCompat
import com.notesapp.compressify.R
import com.notesapp.compressify.domain.useCase.CompressAndSaveImagesUseCase
import com.notesapp.compressify.ui.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

class ImageCompressionService : Service() {

    private val binder = ImageCompressionBinder()

    private val compressAndSaveImagesUseCase = CompressAndSaveImagesUseCase()
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, IMAGE_COMPRESSION_NOTIFICATION_ID)
            .setContentTitle("PixPress")
            .setContentText("Compressing images")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
        startForeground(SERVICE_ID, notification)
        val imagesToOptions = intent?.getParcelableArrayListExtra(
            IMAGE_TO_OPTIONS
        ) ?: emptyList<ImageCompressionModel>()

        CoroutineScope(Dispatchers.IO).launch {
            compressAndSaveImagesUseCase.launch(
                CompressAndSaveImagesUseCase.Params(
                    imagesToOptions = imagesToOptions
                )
            )
        }.invokeOnCompletion {
            stopForeground(STOP_FOREGROUND_REMOVE)
            val completionNotification = NotificationUtil.createCompletionNotification(
                this,
                "${imagesToOptions.size} Images compressed"
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(1, completionNotification)
            stopSelf()
        }
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                IMAGE_COMPRESSION_NOTIFICATION_ID, IMAGE_COMPRESSION_NOTIFICATION_ID,
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
    data class ImageCompressionModel(
        val uri: Uri,
        val resolution: Float,
        val quality: Float,
        val deleteOriginal: Boolean
    ) : Parcelable

    inner class ImageCompressionBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods.
        fun getService(): ImageCompressionService = this@ImageCompressionService
    }

    companion object {
        const val TAG = "ImageCompressionService"
        private const val IMAGE_TO_OPTIONS = "com.notesapp.compressify.service.imagesToOptions"
        private const val IMAGE_COMPRESSION_NOTIFICATION_ID = "Image Compression"
        private const val SERVICE_ID = 100
        fun getIntent(
            context: Context,
            imagesToOptions: List<Pair<Uri, MainViewModel.ImageCompressionOptions>>
        ) = Intent(context, ImageCompressionService::class.java).apply {
            val imageCompressionModels = imagesToOptions.map {
                ImageCompressionModel(
                    uri = it.first,
                    resolution = it.second.resolution,
                    quality = it.second.quality,
                    deleteOriginal = it.second.deleteOriginal
                )
            }
            putParcelableArrayListExtra(
                IMAGE_TO_OPTIONS,
                ArrayList(imageCompressionModels)
            )
        }
    }

}