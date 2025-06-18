package org.example.kmpsamples.presentation.composePreviews

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.hotmovies.presentation.theme.CustomMaterialTheme
import org.example.kmpsamples.presentation.fillMaxSizeModifier
import org.example.kmpsamples.presentation.transitions.ListItem
import org.example.kmpsamples.presentation.transitions.TransitionListScreen

@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun TransitionListPreview() {
    CustomMaterialTheme {
        Surface(fillMaxSizeModifier.safeDrawingPadding()) {
            SharedTransitionLayout(fillMaxSizeModifier) {
                val items = remember {
                    listOf(
                        ListItem(1, "Fruits", Color.DarkGray),
                        ListItem(2, "Fruits", Color.Red),
                        ListItem(3, "Fruits", Color.DarkGray),
                        ListItem(4, "Fruits", Color.Blue),
                        ListItem(5, "Fruits", Color.Yellow),
                        ListItem(6, "Fruits", Color.Blue),
                        ListItem(7, "Vegetables", Color.Green),
                        ListItem(8, "Vegetables", Color.Cyan),
                        ListItem(9, "Vegetables", Color.Green),
                        ListItem(10, "Vegetables", Color.Cyan),
                        ListItem(11, "Dairy", Color.Magenta),
                        ListItem(12, "Dairy", Color.Magenta),
                        ListItem(13, "Dairy", Color.Red),
                        ListItem(14, "Dairy", Color.Magenta),
                        ListItem(15, "Dairy", Color.White),
                        ListItem(16, "Dairy", Color.Yellow),
                        ListItem(17, "Dairy", Color.Yellow)
                    )
                }
                AnimatedContent(fillMaxSizeModifier) {
                    TransitionListScreen(
                        fillMaxSizeModifier,
                        this@SharedTransitionLayout,
                        this@AnimatedContent,
                        items
                    ) {}
                }
            }
        }
    }
}