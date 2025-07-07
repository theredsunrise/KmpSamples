package org.example.kmpsamples.presentation.video

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kmpsamples.ui.layerview.UILayerView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.kmpsamples.presentation.video.viewModel.VideoLooperViewModel
import org.example.kmpsamples.presentation.video.viewModel.VideoLooperViewModel.VideoLooperCommand.PAUSE
import org.example.kmpsamples.presentation.video.viewModel.VideoLooperViewModel.VideoLooperCommand.START

@OptIn(ExperimentalForeignApi::class)
actual class VideoLooperViewFactory : VideoLooperViewFactoryInterface {

    @Composable
    actual override fun Create(
        modifier: Modifier,
        item: VideoLooperViewModel.VideoUIState,
        onVideoLooperState: (state: VideoLooperState) -> Unit
    ) {
        val commandState by rememberUpdatedState(item.command)
        val onVideoLooperStateCallback by rememberUpdatedState(onVideoLooperState)

        val coroutineScope = rememberCoroutineScope()
        val videoLooperController = remember { IosVideoLooperController() }
        val videoLooperState by videoLooperController.state.collectAsStateWithLifecycle()

        LaunchedEffect(videoLooperState) {
            println("**** Looper state: $videoLooperState")
            onVideoLooperStateCallback(videoLooperState)
        }
        if (videoLooperController.isReady()) {
            LifecycleResumeEffect(commandState) {
                when (commandState) {
                    START -> videoLooperController.play()
                    PAUSE -> videoLooperController.pause()
                }
                onPauseOrDispose { }
            }
        }
        DisposableEffect(Unit) {
            onDispose {
                coroutineScope.cancel()
                videoLooperController.dispose()
            }
        }
        UIKitView(
            factory = {
                val view = UILayerView()
                coroutineScope.launch {
                    delay(120)
                    println("**** Load UIKitView ${item.id}")
                    videoLooperController.load(item.url)
                    videoLooperController.attachToView(view)
                }
                view
            },
            onRelease = {
                println("**** Release UIKitView ${item.id}")
                videoLooperController.detachFromView(it)
            },
            modifier = modifier
        )
    }

    actual override fun dispose() {
    }
}

