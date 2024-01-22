package com.notesapp.compressify.ui.components.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notesapp.compressify.R
import com.notesapp.compressify.domain.model.CategoryModel
import com.notesapp.compressify.ui.components.home.common.BottomBar
import com.notesapp.compressify.ui.components.home.common.CustomCircularProgress
import com.notesapp.compressify.ui.theme.primaryColor
import com.notesapp.compressify.ui.theme.primaryTintedColor
import com.notesapp.compressify.util.FileUtil
import com.notesapp.compressify.util.UIEvent
import com.notesapp.compressify.util.getFormattedSize
import com.notesapp.compressify.domain.model.NavigationRoutes

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    categories: List<CategoryModel>,
    onUIEvent: (UIEvent) -> Unit
) {
    var showMore by remember { mutableStateOf(false) }
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomCircularProgress(
                progress = 64.4f,
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = if (showMore) "Show Less" else "Show More",
                style = MaterialTheme.typography.bodyLarge,
                color = primaryColor,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { showMore = !showMore }

            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color = primaryTintedColor)
        )
        AnimatedVisibility(
            visible = showMore,
            modifier = Modifier,
        ) {
            StorageCategories(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 24.dp),
                categoryModelList = categories
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(modifier = Modifier
                .padding(16.dp)

                .clickable { onUIEvent(UIEvent.Navigate(NavigationRoutes.COMPRESS_IMAGE)) }
            ) {
                Column(modifier = Modifier) {
                    Image(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_compress_image),
                        contentDescription = "Compress Image"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Compress Image", style = MaterialTheme.typography.bodyLarge)
                }
            }

        }
    }

}


@Preview
@Composable
fun PreviewHomeScreen() {
    HomeScreen(categories = listOf(), onUIEvent = {}, modifier = Modifier.fillMaxSize())
}