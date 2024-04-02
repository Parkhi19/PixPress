package com.notesapp.compressify.ui.library

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.notesapp.compressify.domain.model.ImageModel
import com.notesapp.compressify.domain.model.LibraryModel
import com.notesapp.compressify.ui.theme.primaryTintedColor
import com.notesapp.compressify.util.UIEvent
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier,
    notDeletedImages : List<LibraryModel>,
    onUIEvent: (UIEvent) -> Unit
) {
    val horizontalPagerState = rememberPagerState(pageCount = { 2 })
    val selectedItems =  remember {
        mutableStateListOf<Uri>()
    }
    val allItemsSelected by remember {
        derivedStateOf {
            notDeletedImages.all {
                selectedItems.contains(it.originalURI)
            }
        }
    }
    Column(
        modifier = modifier
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {

            Text(
                text = "History",
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        HorizontalPager(
            horizontalPagerState,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            when (it) {
                0 -> {
                    Text(
                        text = "Images",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        textAlign = TextAlign.Center
                    )
                }

                else -> {
                    Text(
                        text = "Videos",
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        Spacer(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(color = primaryTintedColor)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "All items",
                modifier = Modifier.padding(8.dp)
            )
            Checkbox(checked = allItemsSelected , onCheckedChange = {
                selectedItems.clear()
                if(it) {
                    selectedItems.addAll(notDeletedImages.mapNotNull {
                        it.originalURI
                    })
                }
            })
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.weight(1f)) {
            items(notDeletedImages.size) {
                notDeletedImages[it].originalURI?.let { originalUri ->
                    if(File(originalUri.path).exists()){
                        IndividualLibraryScreenCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            image = ImageModel(originalUri),
                            isImageSelected = selectedItems.contains(originalUri),
                            onCheckChange = {
                                if (it) {
                                    selectedItems.add(originalUri)
                                } else {
                                    selectedItems.remove(originalUri)
                                }
                            }
                        )
                    }
                    else{
                        Text(text ="Image not found")
                    }
                }
            }
        }
    }
}

