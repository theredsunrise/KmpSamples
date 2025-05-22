package org.example.kmpsamples.presentation.video

import android.view.LayoutInflater
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.kmpsamples.R
import org.example.kmpsamples.presentation.video.viewModel.VideoLooperViewModel
import org.example.kmpsamples.presentation.video.viewModel.VideoLooperViewModel.VideoLooperCommand.PAUSE
import org.example.kmpsamples.presentation.video.viewModel.VideoLooperViewModel.VideoLooperCommand.START

actual class VideoLooperViewFactory(private val pool: ExoPlayerPoolInterface) :
    VideoLooperViewFactoryInterface {

    @OptIn(UnstableApi::class)
    @Composable
    actual override fun Create(
        modifier: Modifier,
        item: VideoLooperViewModel.VideoUIState,
        onVideoLooperState: (state: VideoLooperState) -> Unit
    ) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()

        val commandState by rememberUpdatedState(item.command)
        val onVideoLooperStateCallback by rememberUpdatedState(onVideoLooperState)
        val videoLooperController = remember { AndroidVideoLooperController(pool) }
        val videoLooperState by videoLooperController.state.collectAsStateWithLifecycle()

        var isAndroidViewInvalidated by remember { mutableStateOf(false) }

        LaunchedEffect(videoLooperState) {
            println("***** looper state: $videoLooperState")
            onVideoLooperStateCallback(videoLooperState)
        }

        if (videoLooperController.isReady()) {
            LifecycleResumeEffect(commandState) {
                when (commandState) {
                    START -> videoLooperController.play()
                    PAUSE -> videoLooperController.pause()
                }
                onPauseOrDispose {}
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                println("***** dispose Compose View ${item.id}")
                coroutineScope.cancel()
            }
        }

        AndroidView(
            factory = {
                val view = LayoutInflater.from(context).inflate(R.layout.custom_player_view, null)
                    .let { it as PlayerView }.also {
                        it.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        it.setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
                        it.setControllerShowTimeoutMs(0)
                        it.setKeepContentOnPlayerReset(true)
                        it.setUseController(false)
                    }
                view
            },
            update = {
                coroutineScope.launch {
                    delay(120)
                    if (isAndroidViewInvalidated) return@launch
                    println("***** load AndroidView ${item.id}")
                    if (videoLooperController.attachToView(it)) {
                        videoLooperController.load(item.url)
                    }
                }
            },
            onReset = {
                println("***** reset AndroidView ${item.id}")
                isAndroidViewInvalidated = true
                videoLooperController.detachFromView(it)
                videoLooperController.clean()
            },
            onRelease = {
                println("***** release AndroidView ${item.id}")
                isAndroidViewInvalidated = true
                videoLooperController.detachFromView(it)
                videoLooperController.clean()
            },
            modifier = modifier
        )
    }

    actual override fun dispose() {
        pool.dispose()
    }
}