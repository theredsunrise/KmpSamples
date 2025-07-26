package org.example.kmpsamples.presentation.composePreviews

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.hotmovies.presentation.theme.CustomMaterialTheme
import kotlinx.coroutines.flow.flowOf
import org.example.kmpsamples.presentation.fillMaxSizeModifier
import org.example.kmpsamples.presentation.deepLinks.DeepLinkScreen
import org.example.kmpsamples.presentation.deepLinks.DeepLinkViewModel.Event.ShowToast

@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Preview
@Composable
private fun TestScreenPreview() {
    CustomMaterialTheme {
        Surface(fillMaxSizeModifier.safeDrawingPadding()) {
            DeepLinkScreen(
                fillMaxSizeModifier,
                "",
                flowOf(ShowToast("Hello world")),
                {}, {})
        }
    }
}