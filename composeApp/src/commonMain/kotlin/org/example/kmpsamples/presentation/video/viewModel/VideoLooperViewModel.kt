package org.example.kmpsamples.presentation.video.viewModel

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kmpsamples.composeapp.generated.resources.Res
import org.example.kmpsamples.presentation.video.viewModel.VideoLooperViewModel.Actions.UpdateCommand
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
class VideoLooperViewModel : ViewModel() {
    enum class VideoLooperCommand {
        START, PAUSE;

        fun nextCommand(): VideoLooperCommand {
            return when (this) {
                START -> PAUSE
                PAUSE -> START
            }
        }
    }

    @Stable
    data class VideoUIState(val id: Int, val url: String, val command: VideoLooperCommand)

    private var _videos = mutableStateListOf<VideoUIState>()
    val videos: List<VideoUIState> = _videos

    init {
        (0..<10).flatMap { (1..7).shuffled() }
            .forEachIndexed { indx, name ->
                _videos.add(
                    VideoUIState(
                        indx,
                        Res.getUri("files/${name}.mp4"),
                        VideoLooperCommand.PAUSE
                    )
                )
            }
    }

    sealed interface Actions {
        data class UpdateCommand(val id: Int, val command: VideoLooperCommand) : Actions
    }

    fun doAction(action: Actions) {
        when (action) {
            is UpdateCommand -> onUpdateCommand(action)
        }
    }

    private fun onUpdateCommand(action: UpdateCommand) {
        val videoIndex = videos.indexOfFirst { it.id == action.id }
        _videos[videoIndex] = _videos[videoIndex].copy(command = action.command)
    }
}