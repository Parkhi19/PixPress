# PixPress Media Compressor - Low-Level Design Document

## 1. System Overview
PixPress is an Android application designed to compress both images and videos while maintaining quality control. The application provides a user-friendly interface for batch processing media files with customizable compression settings.

## 2. Core Features
### 2.1 Media Types Support
- Images (JPG, JPEG, PNG, WebP)
- Videos (Multiple formats supported through Android's MediaStore)

### 2.2 Compression Capabilities
#### Image Compression
- Resolution scaling (0-100% of original)
- Quality adjustment (0-100%)
- Format preservation
- Batch processing support

#### Video Compression
- Resolution scaling (0-100% of original)
- Quality levels:
  - Very Low
  - Low
  - Medium
  - High
  - Very High
- Bitrate control
- Audio preservation option
- Batch processing support

## 3. Technical Architecture

### 3.1 Application Structure
```
app/
├── src/
│   ├── main/
│   │   ├── java/com/notesapp/compressify/
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   └── useCase/
│   │   │   ├── service/
│   │   │   ├── ui/
│   │   │   │   ├── components/
│   │   │   │   └── viewmodel/
│   │   │   └── util/
│   │   └── res/
```

### 3.2 Key Components

#### 3.2.1 Domain Layer
- **Models**:
  - `MediaCategory`: Enum defining media types (IMAGE, VIDEO)
  - `VideoModel`: Data class for video metadata
  - `ImageCompressionOptions`: Compression settings for images
  - `VideoCompressionOptions`: Compression settings for videos

#### 3.2.2 Service Layer
- **ImageCompressionService**: Foreground service for image compression
- **VideoCompressionService**: Foreground service for video compression
  - Uses `lightcompressorlibrary` for video compression
  - Supports background processing
  - Provides progress updates

#### 3.2.3 UI Layer
- **MainViewModel**: Central state management
- **Components**:
  - `CompressVideoOptionsScreen`: Video compression settings UI
  - `CompressIndividualVideoScreen`: Individual video compression UI
  - `IndividualVideoCompressedCard`: Video preview and settings card
  - `CompressionOptionsSlider`: Reusable compression settings slider

I'll expand section 3.3 Data Flow in detail:

## 3.3 Data Flow

### 3.3.1 Media Selection Flow
1. **User Selection**
   - User can select multiple media files through Android's MediaStore
   - Files are picked using `ActivityResultContracts.PickMultipleVisualMedia()`
   - Selection is handled in `MainActivity` and `CompressVideoOptionsScreen`

2. **Media Processing**
   - Selected files are converted to `VideoModel` or `ImageModel` objects
   - Each model contains:
     - URI reference
     - File name
     - File size
     - Thumbnail (generated on the fly)
     - Media-specific metadata (dimensions, bitrate for videos)

### 3.3.2 Compression Configuration Flow
1. **Options Initialization**
   ```kotlin
   // Default values
   const val INITIAL_IMAGE_RESOLUTION = 1f
   const val INITIAL_IMAGE_QUALITY = 0.9f
   const val INITIAL_VIDEO_RESOLUTION = 1f
   val INITIAL_VIDEO_QUALITY = VideoQuality.HIGH
   ```

2. **User Configuration**
   - Resolution adjustment (0-100% of original)
   - Quality selection (5 levels for video, 0-100% for images)
   - Option to delete original files
   - Settings can be applied:
     - Globally to all selected files
     - Individually per file

3. **Configuration Storage**
   - Options are stored in `MainViewModel`
   - Individual options are tracked using `mutableStateMapOf<Uri, CompressionOptions>`
   - Changes trigger UI updates through Compose state management

### 3.3.3 Compression Execution Flow
1. **Service Initialization**
   ```kotlin
   // Service creation with compression parameters
   val intent = VideoCompressionService.getIntent(
       context = CompressApplication.appContext,
       videosToOptions = videosToOptions
   )
   ContextCompat.startForegroundService(CompressApplication.appContext, intent)
   ```

2. **Background Processing**
   - Compression runs in foreground service
   - Progress tracking through `CompressionListener`
   - Multiple files processed sequentially
   - Each file's progress is tracked individually

3. **Progress Updates**
   ```kotlin
   override fun onProgress(index: Int, percent: Float) {
       // Update progress for individual file
       onProgress(percent.roundToInt())
   }
   ```

### 3.3.4 File Management Flow
1. **Input Processing**
   - Original files are read from source URI
   - Metadata is extracted (dimensions, format, etc.)
   - Temporary thumbnails are generated for preview

2. **Compression Execution**
   - For Images:
     ```kotlin
     // Image compression process
     val compressedBitmap = Bitmap.createScaledBitmap(
         bitmap,
         compressedWidth.toInt(),
         compressedHeight.toInt(),
         false
     )
     compressedBitmap.compress(
         compressionType,
         (compressionModel.quality * 100).toInt(),
         outputStream
     )
     ```
   - For Videos:
     ```kotlin
     // Video compression configuration
     Configuration(
         quality = VideoQuality.HIGH,
         videoNames = listOf(compressionModel.uri.pathSegments.last()),
         isMinBitrateCheckEnabled = false,
         disableAudio = false,
         keepOriginalResolution = false,
         videoWidth = width.toDouble(),
         videoHeight = height.toDouble()
     )
     ```

3. **Output Handling**
   - Compressed files are saved to designated directories:
     - Images: `/Pictures/PixPress/Images/`
     - Videos: `/Pictures/PixPress/Videos/`
   - Original files are optionally deleted based on user preference
   - New files are assigned unique names using `FileUtil.getNewImageFile()`

### 3.3.5 State Management Flow
1. **UI State Updates**
   - Compression progress is reflected in UI
   - Individual file progress bars
   - Overall progress tracking
   - Error states and completion notifications

2. **Event Handling**
   ```kotlin
   sealed interface UIEvent {
       sealed interface Videos : UIEvent {
           data class RemoveVideoClicked(val path: String) : Videos
           data class VideoCompressionOptionsApplied(
               val resolution: Float,
               val quality: VideoQuality,
               val deleteOriginal: Boolean
           ) : Videos
           data class OnVideosAdded(val uris: List<Uri>) : Videos
           data class OnStartCompressionClick(
               val videosToOptions: List<Pair<Uri, MainViewModel.VideoCompressionOptions>>
           ): Videos
       }
   }
   ```

3. **Error Handling**
   - Compression failures are caught and reported
   - User is notified of any issues
   - Failed operations can be retried
   - Partial completion is handled gracefully

This detailed data flow ensures efficient processing of media files while maintaining a responsive user interface and proper error handling throughout the compression pipeline.


## 4. Technical Implementation Details

### 4.1 Image Compression Implementation

#### 4.1.1 Compression Options
The image compression options are defined using a data class that holds three key parameters:
- `resolution`: Controls the output image size (1.0 = original size, 0.5 = half size)
- `quality`: Controls the compression level (1.0 = best quality, 0.0 = maximum compression)
- `deleteOriginal`: Flag to remove the original file after compression

```kotlin
data class ImageCompressionOptions(
    val resolution: Float = 1.0f,    // 0-1 scale
    val quality: Float = 0.9f,       // 0-1 scale
    val deleteOriginal: Boolean = false
)
```

#### 4.1.2 Compression Process
The image compression process follows these steps:
1. Extract the original file extension to maintain the same format
2. Load the image and calculate new dimensions based on resolution
3. Create a scaled bitmap
4. Determine the appropriate compression format
5. Compress and save the image

```kotlin
private fun compressImage(compressionModel: ImageCompressionService.ImageCompressionModel): Uri {
    // 1. Get original file extension to maintain format
    val originalExtension = compressionModel.uri.toFile().extension

    // 2. Load and scale bitmap according to resolution
    val bitmap = compressionModel.uri.getBitmap()
    val compressedHeight = bitmap.height * compressionModel.resolution
    val compressedWidth = bitmap.width * compressionModel.resolution
    val compressedBitmap = Bitmap.createScaledBitmap(
        bitmap,
        compressedWidth.toInt(),
        compressedHeight.toInt(),
        false
    )

    // 3. Create output file in the app's directory
    val resultFile = FileUtil.getNewImageFile(".$originalExtension")
    val outputStream = FileOutputStream(resultFile)

    // 4. Select compression format based on file type
    val compressionType = when (originalExtension) {
        "jpg", "jpeg" -> Bitmap.CompressFormat.JPEG
        "png" -> Bitmap.CompressFormat.PNG
        "webp" -> Bitmap.CompressFormat.WEBP
        else -> Bitmap.CompressFormat.JPEG
    }

    // 5. Compress and save with specified quality
    compressedBitmap.compress(
        compressionType,
        (compressionModel.quality * 100).toInt(),
        outputStream
    )
    outputStream.flush()
    outputStream.close()
    return resultFile.toUri()
}
```

#### 4.1.3 Image Processing Utilities
This utility function handles image orientation and rotation based on EXIF data, ensuring images are displayed correctly regardless of how they were captured:

```kotlin
fun Uri.getBitmap(): Bitmap {
    // Load the bitmap from file
    val bitmap = BitmapFactory.decodeFile(toFile().absolutePath)
    
    // Read EXIF data for orientation
    val exifInterface = ExifInterface(toFile().absolutePath)
    val orientation = exifInterface.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )
    
    // Rotate bitmap based on EXIF orientation
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> bitmap.rotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> bitmap.rotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> bitmap.rotate(270f)
        else -> bitmap
    }
}
```

### 4.2 Video Compression Implementation

#### 4.2.1 Compression Options
Video compression options are similar to images but use a different quality scale:
- `resolution`: Same as images (1.0 = original size)
- `quality`: Uses predefined levels instead of a continuous scale
- `deleteOriginal`: Same as images

```kotlin
data class VideoCompressionOptions(
    val resolution: Float = 1.0f,
    val quality: VideoQuality = VideoQuality.HIGH,
    val deleteOriginal: Boolean = false
)

// Predefined quality levels for better control
enum class VideoQuality {
    VERY_LOW,  // Maximum compression
    LOW,
    MEDIUM,
    HIGH,
    VERY_HIGH  // Minimum compression
}
```

#### 4.2.2 Video Processing
The video compression process uses the LightCompressor library and follows these steps:
1. Calculate target dimensions based on resolution
2. Configure compression parameters
3. Start compression with progress tracking

```kotlin
private fun compressVideo(
    compressionModel: VideoCompressionService.VideoCompressionModel,
    onProgress: (percent: Int) -> Unit = {},
    onComplete: () -> Unit = {}
) {
    // 1. Calculate new dimensions
    val (width, height) = compressionModel.run {
        (uri.getVideoWidth() * resolution) to (uri.getVideoHeight() * resolution)
    }

    // 2. Start compression with configuration
    VideoCompressor.start(
        context = CompressApplication.appContext,
        uris = listOf(compressionModel.uri),
        isStreamable = true,
        // Configure storage location
        sharedStorageConfiguration = SharedStorageConfiguration(
            saveAt = SaveLocation.movies,
            subFolderName = "PixPress"
        ),
        // Set compression parameters
        configureWith = Configuration(
            quality = VideoQuality.HIGH,
            videoNames = listOf(compressionModel.uri.pathSegments.last()),
            isMinBitrateCheckEnabled = false,
            disableAudio = false,
            keepOriginalResolution = false,
            videoWidth = width.toDouble(),
            videoHeight = height.toDouble()
        ),
        // Handle compression events
        listener = object : CompressionListener {
            override fun onProgress(index: Int, percent: Float) {
                onProgress(percent.roundToInt())
            }
            override fun onSuccess(index: Int, size: Long, path: String?) {
                onComplete()
            }
            // ... other listener methods
        }
    )
}
```

#### 4.2.3 Video Metadata Extraction
These utility functions extract important video metadata needed for compression:

```kotlin
// Get video width in pixels
fun Uri.getVideoWidth(): Int {
    val mediaMetadataRetriever = MediaMetadataRetriever()
    mediaMetadataRetriever.setDataSource(CompressApplication.appContext, this)
    return mediaMetadataRetriever.extractMetadata(
        MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH
    )?.toInt() ?: 0
}

// Get video height in pixels
fun Uri.getVideoHeight(): Int {
    val mediaMetadataRetriever = MediaMetadataRetriever()
    mediaMetadataRetriever.setDataSource(CompressApplication.appContext, this)
    return mediaMetadataRetriever.extractMetadata(
        MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT
    )?.toInt() ?: 0
}

// Get video bitrate in KB/s
fun Uri.getVideoBitrate(): Int {
    val mediaMetadataRetriever = MediaMetadataRetriever()
    mediaMetadataRetriever.setDataSource(CompressApplication.appContext, this)
    return mediaMetadataRetriever.extractMetadata(
        MediaMetadataRetriever.METADATA_KEY_BITRATE
    )?.let { it.toInt() / (8 * 1024) }?.toInt() ?: 0
}
```

### 4.3 Storage Management Implementation

#### 4.3.1 File System Operations
The FileUtil object handles all file system operations, including creating directories and managing file paths:

```kotlin
object FileUtil {
    // Create a new image file in the app's directory
    fun getNewImageFile(fileExtension: String): File {
        val directory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "/PixPress/Images"
        ).apply {
            if (!exists()) {
                mkdirs()
            }
        }
        return File.createTempFile("IMG_", fileExtension, directory)
    }

    // Get the video storage directory
    val videoFilesDirectory: File
        get() {
            val directory = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "/PixPress/Videos"
            ).apply {
                if (!exists()) {
                    mkdirs()
                }
            }
            return directory
        }
}
```

#### 4.3.2 Storage Monitoring
These functions help monitor device storage capacity and usage:

```kotlin
object FileUtil {
    // Get total storage size in bytes
    val totalStorageSize: Long
        get() {
            val stat = StatFs(Environment.getExternalStorageDirectory().path)
            return stat.blockSizeLong * stat.blockCountLong
        }

    // Get available storage space in bytes
    val availableStorageSize: Long
        get() {
            val stat = StatFs(Environment.getExternalStorageDirectory().path)
            return stat.blockSizeLong * stat.availableBlocksLong
        }

    // Calculate used storage space
    val occupiedStorageSize: Long
        get() {
            return totalStorageSize - availableStorageSize
        }

    // Calculate storage usage percentage
    val occupiedStoragePercentage: Float
        get() {
            return (occupiedStorageSize.toFloat() / totalStorageSize.toFloat()) * 100
        }
}
```

### 4.4 Service Implementation

#### 4.4.1 Service Configuration
The services are declared in the Android Manifest with specific configurations:

```kotlin
// AndroidManifest.xml
<service
    android:name=".service.ImageCompressionService"
    android:foregroundServiceType="dataSync"
    android:exported="false"/>

<service
    android:name=".service.VideoCompressionService"
    android:foregroundServiceType="dataSync"
    android:exported="false"/>
```

#### 4.4.2 Service Initialization
The service initialization code handles setting up the compression parameters and starting the service:

```kotlin
companion object {
    // Constants for service configuration
    const val TAG = "VideoCompressionService"
    private const val VIDEO_TO_OPTIONS = "com.notesapp.compressify.service.videosToOptions"
    private const val VIDEO_COMPRESSION_NOTIFICATION_ID = "Video Compression"
    private const val SERVICE_ID = 101

    // Create and configure service intent
    fun getIntent(
        context: Context,
        videosToOptions: List<Pair<Uri, MainViewModel.VideoCompressionOptions>>
    ) = Intent(context, VideoCompressionService::class.java).apply {
        // Convert options to compression models
        val videoCompressionModels = videosToOptions.map {
            VideoCompressionModel(
                uri = it.first,
                resolution = it.second.resolution,
                quality = it.second.quality,
                deleteOriginal = it.second.deleteOriginal
            )
        }
        // Add models to intent extras
        putParcelableArrayListExtra(
            VIDEO_TO_OPTIONS,
            ArrayList(videoCompressionModels)
        )
    }
}
```

This implementation provides a robust foundation for media compression with proper error handling, progress tracking, and efficient resource management.

## 5. Performance Considerations

### 5.1 Memory Management

#### 5.1.1 Thumbnail Generation
The app generates thumbnails efficiently to reduce memory usage while providing previews:

```kotlin
// Efficient thumbnail generation with size reduction
fun Uri.createImageThumbnail(reduceFactor: Int = 20): Bitmap {
    val thumbnail = getBitmap().run {
        Bitmap.createScaledBitmap(
            this, 
            width / reduceFactor, 
            height / reduceFactor, 
            false
        )
    }
    return thumbnail
}

// Video thumbnail generation with error handling
fun Uri.createVideoThumbnail(): Bitmap {
    return try {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(CompressApplication.appContext, this)
        mediaMetadataRetriever.frameAtTime ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8)
    } catch (e: Exception) {
        Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8)
    }
}
```

#### 5.1.2 Bitmap Recycling
Proper bitmap recycling to prevent memory leaks:

```kotlin
class ImageCompressionService : Service() {
    private fun processImage(bitmap: Bitmap) {
        try {
            // Process the bitmap
            val compressedBitmap = compressImage(bitmap)
            // Save the compressed bitmap
            saveCompressedImage(compressedBitmap)
        } finally {
            // Always recycle the bitmap
            if (!bitmap.isRecycled) {
                bitmap.recycle()
            }
        }
    }
}
```

#### 5.1.3 Efficient File Handling
Stream-based file operations to minimize memory usage:

```kotlin
private fun saveCompressedImage(bitmap: Bitmap, outputFile: File) {
    FileOutputStream(outputFile).use { outputStream ->
        bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            quality,
            outputStream
        )
    }
}
```

### 5.2 Background Processing

#### 5.2.1 Foreground Service Implementation
The app uses foreground services to ensure reliable compression even when the app is in the background:

```kotlin
class VideoCompressionService : Service() {
    override fun onCreate() {
        super.onCreate()
        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Compression Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Start as foreground service with notification
        startForeground(
            NOTIFICATION_ID,
            createNotification("Compressing videos...")
        )
        return START_NOT_STICKY
    }
}
```

#### 5.2.2 Progress Tracking
Efficient progress tracking for multiple files:

```kotlin
class CompressionProgressTracker {
    private val progressMap = mutableMapOf<Uri, Int>()
    
    fun updateProgress(uri: Uri, progress: Int) {
        progressMap[uri] = progress
        // Calculate overall progress
        val overallProgress = progressMap.values.average().toInt()
        // Notify UI of progress update
        _progressState.value = overallProgress
    }
}
```

#### 5.2.3 Error Handling and Recovery
Robust error handling with recovery mechanisms:

```kotlin
class VideoCompressionService : Service() {
    private fun handleCompressionError(error: Exception) {
        when (error) {
            is OutOfMemoryError -> {
                // Reduce compression quality and retry
                retryWithReducedQuality()
            }
            is IOException -> {
                // Check storage space and retry
                if (hasEnoughStorage()) {
                    retryCompression()
                } else {
                    notifyStorageError()
                }
            }
            else -> {
                // Log error and notify user
                Log.e(TAG, "Compression failed", error)
                notifyCompressionError()
            }
        }
    }
}
```

### 5.3 Storage Optimization

#### 5.3.1 Directory Management
Efficient directory creation and management:

```kotlin
object FileUtil {
    fun ensureDirectoryExists(path: String): File {
        return File(path).apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    fun getCompressionDirectory(type: MediaType): File {
        val basePath = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        ).absolutePath
        return ensureDirectoryExists("$basePath/PixPress/${type.name}")
    }
}
```

#### 5.3.2 Storage Space Monitoring
Continuous monitoring of available storage:

```kotlin
class StorageMonitor {
    private val storageThreshold = 0.9f // 90% of total storage
    
    fun checkStorageAvailability(): Boolean {
        val usedPercentage = FileUtil.occupiedStoragePercentage
        return usedPercentage < storageThreshold
    }
    
    fun getAvailableSpace(): Long {
        return FileUtil.availableStorageSize
    }
    
    fun estimateCompressionSpace(originalSize: Long): Long {
        // Estimate compressed size (typically 30-70% of original)
        return (originalSize * 0.5).toLong()
    }
}
```

#### 5.3.3 File Size Calculations
Efficient file size handling and formatting:

```kotlin
object FileSizeUtil {
    fun Long.getFormattedSize(): String {
        val kb = (this / 1024.0)
        val mb = kb / 1024f
        val gb = mb / 1024f

        return when {
            gb >= 1 -> "${gb.precised(1)} GB"
            mb >= 1 -> "${mb.precised(1)} MB"
            else -> "${kb.precised(1)} KB"
        }
    }
    
    fun calculateTotalSize(files: List<File>): Long {
        return files.sumOf { it.length() }
    }
    
    fun estimateCompressedSize(originalSize: Long, quality: Float): Long {
        // Rough estimation based on quality setting
        return (originalSize * quality).toLong()
    }
}
```

### 5.4 Performance Monitoring

#### 5.4.1 Compression Metrics
Tracking compression performance:

```kotlin
class CompressionMetrics {
    data class CompressionResult(
        val originalSize: Long,
        val compressedSize: Long,
        val compressionRatio: Float,
        val processingTime: Long
    )
    
    fun calculateCompressionRatio(original: Long, compressed: Long): Float {
        return (original - compressed).toFloat() / original
    }
    
    fun logCompressionMetrics(result: CompressionResult) {
        Log.d("CompressionMetrics", """
            Original Size: ${result.originalSize.getFormattedSize()}
            Compressed Size: ${result.compressedSize.getFormattedSize()}
            Compression Ratio: ${result.compressionRatio * 100}%
            Processing Time: ${result.processingTime}ms
        """.trimIndent())
    }
}
```

#### 5.4.2 Resource Usage Tracking
Monitoring system resource usage during compression:

```kotlin
class ResourceMonitor {
    private val runtime = Runtime.getRuntime()
    
    fun getMemoryUsage(): MemoryUsage {
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        val maxMemory = runtime.maxMemory()
        return MemoryUsage(
            used = usedMemory,
            total = maxMemory,
            percentage = (usedMemory.toFloat() / maxMemory) * 100
        )
    }
    
    fun isMemoryCritical(): Boolean {
        val usage = getMemoryUsage()
        return usage.percentage > 90 // 90% memory usage threshold
    }
}
```

These performance optimizations ensure the app runs efficiently, handles resources properly, and provides a smooth user experience even when processing large media files. The implementation includes proper error handling, resource management, and performance monitoring to maintain stability and reliability.

## 6. User Interface Features

### 6.1 Media Selection

#### 6.1.1 File Picker Implementation
The app lets users pick multiple files at once using Android's built-in picker:

```kotlin
// In MainActivity or relevant screen
val mediaPicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickMultipleVisualMedia(),
    onResult = { uris ->
        // Handle selected files
        viewModel.onMediaSelected(uris)
    }
)

// Launch picker when user taps select button
Button(onClick = {
    mediaPicker.launch(
        PickVisualMediaRequest(
            ActivityResultContracts.PickVisualMedia.ImageAndVideo
        )
    )
}) {
    Text("Select Files")
}
```

#### 6.1.2 Preview Generation
Shows small previews of selected files before compression:

```kotlin
@Composable
fun MediaPreviewCard(
    uri: Uri,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(120.dp)
            .padding(4.dp)
    ) {
        // Show thumbnail
        Image(
            bitmap = uri.createThumbnail().asImageBitmap(),
            contentDescription = "Preview",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Show file size
        Text(
            text = uri.getFileSize().getFormattedSize(),
            modifier = Modifier.padding(4.dp)
        )
        
        // Remove button
        IconButton(
            onClick = onRemove,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(Icons.Default.Close, "Remove")
        }
    }
}
```

### 6.2 Compression Controls

#### 6.2.1 Resolution Slider
A simple slider to adjust output size:

```kotlin
@Composable
fun ResolutionSlider(
    currentResolution: Float,
    onResolutionChange: (Float) -> Unit
) {
    Column {
        Text("Output Size: ${(currentResolution * 100).toInt()}%")
        
        Slider(
            value = currentResolution,
            onValueChange = onResolutionChange,
            valueRange = 0.1f..1f,
            steps = 9
        )
        
        // Show preview dimensions
        Text(
            text = "Will be: ${originalWidth * currentResolution} x ${originalHeight * currentResolution}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
```

#### 6.2.2 Quality Selection
Simple quality picker for videos:

```kotlin
@Composable
fun QualitySelector(
    currentQuality: VideoQuality,
    onQualityChange: (VideoQuality) -> Unit
) {
    Column {
        Text("Quality")
        
        // Simple quality buttons
        Row {
            QualityButton(
                quality = VideoQuality.LOW,
                selected = currentQuality == VideoQuality.LOW,
                onClick = { onQualityChange(VideoQuality.LOW) }
            )
            QualityButton(
                quality = VideoQuality.MEDIUM,
                selected = currentQuality == VideoQuality.MEDIUM,
                onClick = { onQualityChange(VideoQuality.MEDIUM) }
            )
            QualityButton(
                quality = VideoQuality.HIGH,
                selected = currentQuality == VideoQuality.HIGH,
                onClick = { onQualityChange(VideoQuality.HIGH) }
            )
        }
    }
}
```

#### 6.2.3 Original File Option
Simple checkbox to delete original files:

```kotlin
@Composable
fun DeleteOriginalOption(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
        Text("Delete original files after compression")
    }
}
```

### 6.3 Progress Tracking

#### 6.3.1 Individual File Progress
Shows progress for each file being compressed:

```kotlin
@Composable
fun FileProgressCard(
    fileName: String,
    progress: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(fileName)
            
            // Progress bar
            LinearProgressIndicator(
                progress = progress / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            
            Text("$progress%")
        }
    }
}
```

#### 6.3.2 Overall Progress
Shows total compression progress:

```kotlin
@Composable
fun OverallProgress(
    completed: Int,
    total: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Compressing files: $completed of $total",
            style = MaterialTheme.typography.titleMedium
        )
        
        LinearProgressIndicator(
            progress = completed.toFloat() / total,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    }
}
```

#### 6.3.3 Completion Notifications
Simple completion message:

```kotlin
@Composable
fun CompressionComplete(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Compression Complete") },
        text = { Text("Your files have been compressed successfully!") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
```

### 6.4 Error Handling UI

#### 6.4.1 Error Messages
Simple error display:

```kotlin
@Composable
fun ErrorMessage(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Error") },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
```

#### 6.4.2 Retry Option
Simple retry button for failed compressions:

```kotlin
@Composable
fun RetryButton(
    onRetry: () -> Unit
) {
    Button(
        onClick = onRetry,
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Try Again")
    }
}
```

The UI is designed to be simple and user-friendly, with clear controls and feedback. It focuses on making the compression process easy to understand and control, while providing clear information about what's happening at each step.

## 7. Security and Permissions

### 7.1 Required Permissions
- Storage access
- Media read/write
- Foreground service

### 7.2 Data Handling
- Secure file operations
- Proper URI handling
- Safe file deletion

## 8. Dependencies
- AndroidX Compose for UI
- LightCompressor library for video compression
- Kotlin Coroutines for async operations
- Android MediaStore for file access

## 9. Future Considerations
1. Cloud storage integration
2. Additional media format support
3. Advanced compression algorithms
4. Batch operation optimization
5. Custom compression presets

This LLD provides a comprehensive overview of the PixPress media compressor application, detailing its architecture, components, and implementation specifics. The design follows modern Android development practices and provides a solid foundation for future enhancements.
