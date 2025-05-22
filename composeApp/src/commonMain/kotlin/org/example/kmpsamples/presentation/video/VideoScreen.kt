package org.example.kmpsamples.presentation.video

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.example.kmpsamples.presentation.fillMaxSizeModifier
import org.example.kmpsamples.presentation.video.VideoLooperState.ERROR
import org.example.kmpsamples.presentation.video.VideoLooperState.UNLOADED
import org.example.kmpsamples.presentation.video.viewModel.VideoLooperViewModel.Actions
import org.example.kmpsamples.presentation.video.viewModel.VideoLooperViewModel.VideoUIState

@Composable
fun VideoScreen(
    modifier: Modifier,
    videos: List<VideoUIState>,
    videoLooperViewFactory: VideoLooperViewFactoryInterface,
    doAction: (action: Actions) -> Unit
) {
    LazyColumn(modifier) {
        items(videos, key = { it.id }) { videoItem ->
            VideoItem(videoItem, videoLooperViewFactory, doAction)
        }
    }
}

@Composable
private fun VideoItem(
    videoItem: VideoUIState,
    videoLooperViewFactory: VideoLooperViewFactoryInterface,
    doAction: (action: Actions) -> Unit
) {
    var looperState by remember { mutableStateOf<VideoLooperState>(UNLOADED) }
    Surface(
        Modifier.fillMaxWidth().requiredHeight(150.dp)
            .clickable(enabled = !looperState.isNotReady) {
                doAction(Actions.UpdateCommand(videoItem.id, videoItem.command.nextCommand()))
            },
        shape = MaterialTheme.shapes.medium
    ) {
        Box(fillMaxSizeModifier) {
            videoLooperViewFactory.Create(
                fillMaxSizeModifier.background(MaterialTheme.colorScheme.background), videoItem
            ) {
                looperState = it
            }
            if (looperState.isNotReady) {
                Box(fillMaxSizeModifier.background(Color.Gray)) {
                    when (val currentLooperState = looperState) {
                        is UNLOADED -> {
                            CircularProgressIndicator(Modifier.align(Alignment.Center))
                        }

                        is ERROR -> {
                            Text(
                                currentLooperState.reason,
                                Modifier.align(Alignment.Center)
                            )
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}