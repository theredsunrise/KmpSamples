package org.example.kmpsamples.presentation.video

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.example.kmpsamples.presentation.video.viewModel.VideoLooperViewModel

sealed interface VideoLooperState {
    data object UNLOADED : VideoLooperState
    data object LOADED : VideoLooperState
    data object PLAYING : VideoLooperState
    data object PAUSED : VideoLooperState
    data class ERROR(val reason: String) : VideoLooperState

    val isNotReady: Boolean
        get() = when (this) {
            is UNLOADED, is ERROR -> true
            else -> false
        }
}

interface VideoLooperViewFactoryInterface {
    @Composable
    fun Create(
        modifier: Modifier,
        item: VideoLooperViewModel.VideoUIState,
        onVideoLooperState: (state: VideoLooperState) -> Unit
    )

    fun dispose()
}

expect class VideoLooperViewFactory : VideoLooperViewFactoryInterface {
    @Composable
    override fun Create(
        modifier: Modifier,
        item: VideoLooperViewModel.VideoUIState,
        onVideoLooperState: (state: VideoLooperState) -> Unit
    )

    override fun dispose()
}
