package com.notesapp.compressify.ui.library

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.notesapp.compressify.domain.model.ImageModel
import com.notesapp.compressify.domain.model.LibraryModel
import com.notesapp.compressify.ui.components.home.common.PrimaryButtonOutlined
import com.notesapp.compressify.util.UIEvent
import java.io.File

@Composable
fun ImageLibraryScreen(
    modifier: Modifier = Modifier, notDeletedImages: List<LibraryModel>,
    onUIEvent: (UIEvent) -> Unit
) {

    val selectedItems = remember {
        mutableStateListOf<String>()
    }
    val allItemsSelected by remember {
        derivedStateOf {
            notDeletedImages.isNotEmpty() && notDeletedImages.all {
                selectedItems.contains(it.originalURI.toString())
            }
        }
    }
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "All items",
                modifier = Modifier.padding(8.dp)
            )
            Checkbox(
                checked = allItemsSelected,
                onCheckedChange = {
                    selectedItems.clear()
                    if (it) {
                        selectedItems.addAll(notDeletedImages.mapNotNull {
                            it.originalURI.toString()
                        })
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.weight(1f)) {
            items(notDeletedImages.size) {
                notDeletedImages[it].originalURI?.let { originalUri ->
                    if (File(originalUri.path).exists()) {
                        IndividualLibraryScreenCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            image = ImageModel(originalUri),
                            isImageSelected = selectedItems.contains(originalUri.toString()),
                            onCheckChange = {
                                if (it) {
                                    selectedItems.add(originalUri.toString())
                                } else {
                                    selectedItems.remove(originalUri.toString())
                                }
                            }
                        )
                    } else {
                        Text(text = "Image not found")
                    }
                }
            }
        }
        PrimaryButtonOutlined(modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(), buttonText = "Delete", onClick = {
            onUIEvent(
                UIEvent.Images.OnDeleteSelectedImagesClick(
                    selectedItems.map{uri->
                        Uri.parse(uri)
                    }
                )
            )
        })
    }
}