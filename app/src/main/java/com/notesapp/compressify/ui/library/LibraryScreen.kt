package com.notesapp.compressify.ui.library

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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.notesapp.compressify.R
import com.notesapp.compressify.ui.theme.primaryTintedColor
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Row(modifier = modifier.fillMaxWidth()){

            Text(text = "History", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        }
        Row (modifier = modifier
            .clickable{}
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween){
            Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_compress_image),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .size(40.dp)
                )
                Text(text = "Images", modifier = Modifier.padding(vertical = 8.dp))

            }

            Row(modifier = modifier
                .clickable {  }
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween){
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_compress_video),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .size(40.dp)
                )
                Text(text = "Videos", modifier = Modifier.padding(vertical = 8.dp))

            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = primaryTintedColor)
            )

        }
    }
}
