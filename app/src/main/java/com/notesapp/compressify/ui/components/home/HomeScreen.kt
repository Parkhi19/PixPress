package com.notesapp.compressify.ui.components.home

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.notesapp.compressify.ui.components.home.common.BottomBar
import com.notesapp.compressify.util.UIEvent

@Composable
fun HomeScreen(modifier: Modifier = Modifier, onUIEvent: (UIEvent) -> Unit) {
    Scaffold(
        modifier = modifier,
        topBar = {},
        bottomBar = {
            BottomBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 56.dp),
                onUIEvent = onUIEvent
            )
        }) {
        Surface(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {

        }
    }
}