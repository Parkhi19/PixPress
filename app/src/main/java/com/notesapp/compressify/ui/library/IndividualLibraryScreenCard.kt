package com.notesapp.compressify.ui.library

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.notesapp.compressify.domain.model.ImageModel
import com.notesapp.compressify.util.createImageThumbnail

@Composable
fun IndividualLibraryScreenCard(
    modifier: Modifier = Modifier,
    image: Uri
) {
    ElevatedCard(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                val (detailsRef, imageRef, selectedImage) = createRefs()
                Image(
                    bitmap = image.createImageThumbnail(20).asImageBitmap(),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .fillMaxHeight(0.4f)
                        .padding(8.dp)
                        .constrainAs(imageRef) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )
              Checkbox(
                  modifier = Modifier
                      .fillMaxWidth()
                      .fillMaxHeight()
                      .constrainAs(selectedImage) {
                          top.linkTo(parent.top)
                          bottom.linkTo(parent.bottom)
                          end.linkTo(parent.end)
                      },
                  checked = false,
                  onCheckedChange ={}
              )
            }
        }
    }
}