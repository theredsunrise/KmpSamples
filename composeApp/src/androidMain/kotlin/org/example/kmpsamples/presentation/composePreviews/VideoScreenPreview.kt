package org.example.kmpsamples.presentation.composePreviews

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.hotmovies.presentation.theme.CustomMaterialTheme
import org.example.kmpsamples.presentation.fillMaxSizeModifier
import org.example.kmpsamples.presentation.video.VideoLooperState
import org.example.kmpsamples.presentation.video.VideoLooperViewFactoryInterface
import org.example.kmpsamples.presentation.video.VideoScreen
import org.example.kmpsamples.presentation.video.viewModel.VideoLooperViewModel.VideoLooperCommand
import org.example.kmpsamples.presentation.video.viewModel.VideoLooperViewModel.VideoUIState

private val mockVideoLooperView = object : VideoLooperViewFactoryInterface {

    @Composable
    override fun Create(
        modifier: Modifier,
        item: VideoUIState,
        onVideoLooperState: (state: VideoLooperState) -> Unit
    ) {
        onVideoLooperState(VideoLooperState.ERROR("Error"))
    }

    override fun dispose() {
    }
}

private fun createMockItem(id: Int) = VideoUIState(id, "files/$id.mp4", VideoLooperCommand.PAUSE)

@Preview
@Composable
private fun PreviewCustomDialog() {
    CustomMaterialTheme {
        Surface(fillMaxSizeModifier.safeDrawingPadding()) {
            val items = remember { listOf(createMockItem(1), createMockItem(2)) }
            VideoScreen(fillMaxSizeModifier, items, mockVideoLooperView) {}
        }
    }
}
