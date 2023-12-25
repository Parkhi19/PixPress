package com.notesapp.compressify.ui.components.image

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.notesapp.compressify.domain.model.ImageModel

@Composable
fun CompressOptionsScreen(modifier: Modifier = Modifier, selectedImages :List<ImageModel>) {
   Column (modifier = modifier) {
     LazyVerticalGrid(columns = GridCells.Fixed(2)){
            items(selectedImages.size) {
                ImagePreviewCard(image = selectedImages[it])
            }
     }

   }
}