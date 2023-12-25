package com.notesapp.compressify

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.notesapp.compressify.ui.theme.CompressifyTheme

private lateinit var compressedImageUri : Uri
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompressifyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppContent()
                }
            }
        }
    }
}

@Composable
fun AppContent() {

    var selectedMediaUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val selectedMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
                uri -> selectedMediaUri = uri
        }
    )
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SelectFileButton(onClick = {
            selectedMediaLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
            )
        })
    }

    selectedMediaUri?.let { uri ->
        val activity = LocalContext.current
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, uri)
            val compressedHeight = bitmap.height / 2
            val compressedWidth = bitmap.width / 2
            val compressedBitmap = Bitmap.createScaledBitmap(bitmap, compressedWidth ,compressedHeight, false)
            val path = MediaStore.Images.Media.insertImage(
                activity.contentResolver,
                compressedBitmap,
                "Title",
                null
            )
            println("path: $path")
//            activity.
//            openFileOutput("compressedImage", MODE_PRIVATE).use {it->
//                compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
//            }
//            compressedImageUri = Uri.parse(path.toString())
        } catch (e: Exception) {
            Log.e("MainActivity", "Error loading bitmap: ${e.message}")
        }
    }

}

@Composable
fun SelectFileButton(onClick: () -> Unit) {
    ElevatedButton(
        onClick = onClick,
        content = {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(0.7f)
            ) {
                Text(text = "Select a File to Compress", textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)
            }
        }
    )
}
