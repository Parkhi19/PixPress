package com.notesapp.compressify.ui.library

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.notesapp.compressify.domain.model.ImageModel
import com.notesapp.compressify.domain.model.LibraryModel
import com.notesapp.compressify.ui.components.home.common.PrimaryButtonOutlined
import com.notesapp.compressify.ui.theme.primaryTintedColor
import com.notesapp.compressify.util.UIEvent
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier,
    notDeletedImages: List<LibraryModel>,
    onUIEvent: (UIEvent) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val horizontalPagerState = rememberPagerState(pageCount = { 2 })

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
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(color = if (horizontalPagerState.currentPage == 0) primaryTintedColor else Color.Transparent)
                    .clickable {
                        coroutineScope.launch {
                            horizontalPagerState.animateScrollToPage(0)
                        }
                    },
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "Images",
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(color = if (horizontalPagerState.currentPage == 1) primaryTintedColor else Color.Transparent)
                    .clickable {
                        coroutineScope.launch {
                            horizontalPagerState.animateScrollToPage(1)
                        }
                    },
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "Videos",
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }

        }
        HorizontalPager(
            horizontalPagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (it) {
                0 -> {
                    ImageLibraryScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        notDeletedImages = notDeletedImages,
                        onUIEvent = onUIEvent
                    )
                }

                else -> {
                    Text(
                        text = "Videos",
                        modifier = Modifier
                            .fillMaxSize(),
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
    }
}


