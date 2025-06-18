package org.example.kmpsamples.presentation.composePreviews

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.hotmovies.presentation.theme.CustomMaterialTheme
import org.example.kmpsamples.presentation.fillMaxSizeModifier
import org.example.kmpsamples.presentation.transitions.TransitionDetailScreen

@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Preview
@Composable
private fun TransitionListPreview() {
    CustomMaterialTheme {
        Surface(fillMaxSizeModifier.safeDrawingPadding()) {
            SharedTransitionLayout(fillMaxSizeModifier) {
                AnimatedContent(fillMaxSizeModifier) {
                    TransitionDetailScreen(
                        fillMaxSizeModifier,
                        this@SharedTransitionLayout,
                        this@AnimatedContent,
                        5
                    )
                }
            }
        }
    }
}