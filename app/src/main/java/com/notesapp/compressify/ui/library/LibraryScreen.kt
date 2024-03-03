package com.notesapp.compressify.ui.library

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.notesapp.compressify.R
import com.notesapp.compressify.domain.model.ImageModel
import com.notesapp.compressify.domain.model.LibraryModel
import com.notesapp.compressify.ui.theme.primaryTintedColor
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier,
    initialAllItemsSelected: Boolean,
    notDeletedImages : List<LibraryModel>
) {
    val horizontalPagerState = rememberPagerState(pageCount = { 2 })
    val allItemsSelected by remember { mutableStateOf(initialAllItemsSelected) }
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
            Checkbox(checked = allItemsSelected , onCheckedChange = {})
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(10) {
                notDeletedImages[it].originalURI?.let { originalUri ->
                    IndividualLibraryScreenCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        image = originalUri
                    )
                }
            }
        }
    }
}

