package org.example.kmpsamples.presentation.video

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kmpsamples.ui.layerview.UILayerView
import kotlinx.cinterop.ExperimentalForeignApi
import org.example.kmpsamples.presentation.video.viewModel.VideoLooperViewModel
import org.example.kmpsamples.presentation.video.viewModel.VideoLooperViewModel.VideoLooperCommand.PAUSE
import org.example.kmpsamples.presentation.video.viewModel.VideoLooperViewModel.VideoLooperCommand.START

@OptIn(ExperimentalForeignApi::class)
actual fun videoLooperViewFactory() = object : VideoLooperViewFactoryInterface {

    @Composable
    override fun Create(
        modifier: Modifier,
        item: VideoLooperViewModel.VideoUIState,
        onVideoLooperState: (state: VideoLooperState) -> Unit
    ) {
        val commandState by rememberUpdatedState(item.command)
        val onVideoLooperStateCallback by rememberUpdatedState(onVideoLooperState)

        val videoLooperController = remember { IosVideoLooperController() }
        val videoLooperState by videoLooperController.state.collectAsStateWithLifecycle()

        LaunchedEffect(videoLooperState) {
            onVideoLooperStateCallback(videoLooperState)
        }
        if (videoLooperController.isReady()) {
            LifecycleResumeEffect(commandState) {
                when (commandState) {
                    START -> videoLooperController.start()
                    PAUSE -> videoLooperController.pause()
                }
                onPauseOrDispose { }
            }
        }
        DisposableEffect(Unit) {
            onDispose {
                videoLooperController.dispose()
            }
        }
        UIKitView(
            factory = {
                val view = UILayerView()
                videoLooperController.load(item.url)
                videoLooperController.attachToView(view)
                view
            },
            onRelease = {
                videoLooperController.detachFromView(it)
            },
            modifier = modifier
        )
    }
}
