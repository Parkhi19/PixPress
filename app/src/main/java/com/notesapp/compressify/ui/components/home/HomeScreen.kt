package com.notesapp.compressify.ui.components.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.notesapp.compressify.R
import com.notesapp.compressify.domain.model.CategoryModel
import com.notesapp.compressify.domain.model.NavigationRoutes
import com.notesapp.compressify.ui.components.home.common.CustomCircularProgress
import com.notesapp.compressify.ui.components.home.common.PrimaryButtonOutlined
import com.notesapp.compressify.ui.theme.primaryColor
import com.notesapp.compressify.ui.theme.primaryTintedColor
import com.notesapp.compressify.util.FileUtil
import com.notesapp.compressify.util.UIEvent
import com.notesapp.compressify.util.getFormattedSize

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    categories: List<CategoryModel>,
    onCompressImageClick: () -> Unit,
    onCompressVideoClick: () -> Unit,
    onLibraryButtonClick : ()-> Unit,
    onUIEvent: (UIEvent) -> Unit
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomCircularProgress(
                progress = FileUtil.occupiedStoragePercentage,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .size(96.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Total Space:", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = FileUtil.totalStorageSize.getFormattedSize(),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = Bold
                        )
                    )

                }
                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Free Space:", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = FileUtil.availableStorageSize.getFormattedSize(),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = primaryColor,
                            fontWeight = Bold
                        )
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color = primaryTintedColor)
        )
        StorageCategories(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 24.dp),
            categoryModelList = categories
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            item {
                CompressOptionItem(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clickable {
                            onCompressImageClick()
                        },
                    imageVector = R.drawable.ic_compress_image,
                    compressOption = "Compress\nImages"
                )
            }
            item {
                CompressOptionItem(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clickable {
                            onCompressVideoClick()
                        },
                    imageVector = R.drawable.ic_compress_video,
                    compressOption = "Compress\nVideos"
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButtonOutlined(modifier = Modifier.padding(24.dp).fillMaxWidth(), buttonText = "Chaqopy", onClick = {
            onUIEvent(UIEvent.Navigate(NavigationRoutes.LIBRARY))
        })

        }

    }



@Composable
fun CompressOptionItem(
    modifier: Modifier = Modifier,
    imageVector: Int,
    compressOption: String
) {
    Column(
        modifier = modifier.border(
            width = 2.dp,
            color = primaryColor,
            shape = MaterialTheme.shapes.medium
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Image(
            imageVector = ImageVector.vectorResource(id = imageVector),
            contentDescription = "",
            modifier = Modifier
                .padding(vertical = 8.dp)
                .size(40.dp)
        )
        Text(
            text = compressOption,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 12.dp)
                .width(154.dp),
            textAlign = TextAlign.Center,
            maxLines = 2
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}


