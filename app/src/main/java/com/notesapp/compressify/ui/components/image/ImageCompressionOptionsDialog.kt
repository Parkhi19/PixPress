package com.notesapp.compressify.ui.components.image

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.notesapp.compressify.ui.components.home.common.CompressionOptionsSlider
import com.notesapp.compressify.ui.components.home.common.PrimaryButton
import com.notesapp.compressify.ui.theme.primaryTintedColor
import kotlinx.coroutines.launch


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ImageCompressionOptionsDialog(
    modifier: Modifier,
    onDismiss: () -> Unit,
    initialResolution : Float,
    initialQuality : Float,
    initialDeleteOriginal : Boolean,
    onConfirm: (Float, Float, Boolean) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(onDismissRequest = { onDismiss() }, sheetState = sheetState) {
        val textColor = MaterialTheme.colorScheme.onBackground
        var resolution by mutableFloatStateOf(initialResolution)
        var quality by mutableFloatStateOf(initialQuality)
        var deleteOriginal by mutableStateOf(initialDeleteOriginal)
        Card(
            shape = RoundedCornerShape(
                size = 8.dp
            ),
            modifier = modifier,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Compress Options",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = textColor
                    )
                }
                Spacer(
                    modifier = Modifier
                        .height(2.dp)
                        .fillMaxWidth()
                        .background(primaryTintedColor)
                        .padding(vertical = 12.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    CompressionOptionsSlider(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Select Resolution",
                        labelValue = "${(resolution * 100).toInt()} %",
                        value = resolution,
                    ) {
                        resolution = it
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    CompressionOptionsSlider(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Select Quality",
                        labelValue = "${(quality * 100).toInt()} %",
                        value = quality,
                    ) {
                        quality = it
                    }
                }
                Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = deleteOriginal,
                        onCheckedChange = { deleteOriginal = it },
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Text(
                        text = "Delete Original",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp),
                        color = textColor
                    )
                }
                PrimaryButton(
                    onClick = {
                        onConfirm(resolution, quality, deleteOriginal)
                        coroutineScope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onDismiss()
                            }
                        }
                    },
                    buttonText = "Apply",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                )
            }
        }
    }
}