package com.notesapp.compressify.ui.components.home.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.notesapp.compressify.ui.theme.primaryColor

@Composable
fun CompressionOptionsSlider(
    modifier: Modifier = Modifier,
    label: String,
    labelValue: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    val textColor = MaterialTheme.colorScheme.onBackground
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp),
                color = textColor
            )
            Text(
                text = labelValue,
                style = MaterialTheme.typography.bodySmall,
                color = textColor
            )
        }
        Slider(
            value = value,
            valueRange = 0.1f..1f,
            colors = SliderDefaults.colors(
                activeTrackColor = primaryColor,
                thumbColor = primaryColor,
                inactiveTrackColor = primaryColor.copy(alpha = 0.7f)
            ),
            onValueChange = onValueChange
        )
    }
}