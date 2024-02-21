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
import androidx.lifecycle.asLiveData
import com.notesapp.compressify.R
import com.notesapp.compressify.data.repository.LibraryRepository
import com.notesapp.compressify.domain.model.LibraryModel
import com.notesapp.compressify.domain.repository.LibraryRepositoryImpl
import com.notesapp.compressify.domain.useCase.AddLibraryItemUseCase
import com.notesapp.compressify.domain.useCase.CompressAndSaveImagesUseCase
import com.notesapp.compressify.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject
class ImageCompressionService : Service() {
//    @Inject lateinit var libraryRepository: LibraryRepository

    private val libraryRepository: LibraryRepository = LibraryRepositoryImpl()

    private val binder = ImageCompressionBinder()
    private val addLibraryItemUseCase = AddLibraryItemUseCase(libraryRepository)
    private val compressAndSaveImagesUseCase = CompressAndSaveImagesUseCase(addLibraryItemUseCase)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val imagesToOptions = intent?.getParcelableArrayListExtra(
            IMAGE_TO_OPTIONS
        ) ?: emptyList<ImageCompressionModel>()
        createNotificationChannel()
        val notificationBuilder =
            NotificationCompat.Builder(this, IMAGE_COMPRESSION_NOTIFICATION_ID)
                .setContentTitle("0 / ${imagesToOptions.size} Images Compressed")
                .setProgress(imagesToOptions.size, 0, false)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.drawable.ic_category_image)
        val notification = notificationBuilder.build()
        startForeground(SERVICE_ID, notification)


        val totalCompressedImages = compressAndSaveImagesUseCase.launchWithFlow(
            CompressAndSaveImagesUseCase.Params(
                imagesToOptions = imagesToOptions
            )
        )

        CoroutineScope(Dispatchers.IO).launch {
            totalCompressedImages.collect {
                notificationBuilder.setContentTitle("$it / ${imagesToOptions.size} Images Compressed")
                    .setProgress(imagesToOptions.size, it, false)
                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(SERVICE_ID, notificationBuilder.build())
                if (it >= imagesToOptions.size) {
                    onComplete(it)
                }
            }
        }
        return START_STICKY
    }

    private fun onComplete(totalSize : Int){
        stopForeground(STOP_FOREGROUND_REMOVE)
        val completionNotification = NotificationUtil.createCompletionNotification(
            this@ImageCompressionService,
            "$totalSize Images compressed"
        )
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, completionNotification)
        stopSelf()
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