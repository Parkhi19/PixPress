package com.notesapp.compressify.ui.components.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notesapp.compressify.R
import com.notesapp.compressify.domain.model.CategoryModel
import com.notesapp.compressify.ui.components.home.common.CustomLinearIndicator
import com.notesapp.compressify.util.getFormattedSize

@Composable
fun StorageCategories(modifier: Modifier = Modifier, categoryModelList: List<CategoryModel>) {
    LazyColumn(modifier = modifier) {
        items(categoryModelList.size) {
            val category = categoryModelList[it]
            StorageCategory(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                iconId = category.iconId,
                categoryName = category.name,
                startingPoint = category.startingPoint,
                endPoint = category.endPoint,
                size = category.size
            )
        }
    }
}

@Composable
fun StorageCategory(
    modifier: Modifier = Modifier,
    iconId: Int,
    categoryName: String,
    startingPoint: Float,
    endPoint: Float,
    size: Long
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            imageVector = ImageVector.vectorResource(id = iconId),
            contentDescription = "",
            modifier = Modifier.width(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = categoryName, style = MaterialTheme.typography.bodyMedium)
                Text(text = size.getFormattedSize(), style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(4.dp))
            CustomLinearIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                startingPoint = startingPoint,
                endPoint = endPoint
            )
        }
    }
}

@Preview
@Composable
fun PrevStorage() {
    StorageCategory(
        iconId = R.drawable.ic_category_video,
        categoryName = "Video",
        startingPoint = 10f,
        endPoint = 20f,
        size = 87563,
        modifier = Modifier
            .fillMaxWidth()
    )
}