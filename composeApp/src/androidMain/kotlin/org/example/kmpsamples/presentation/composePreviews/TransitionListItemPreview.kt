package org.example.kmpsamples.presentation.composePreviews

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hotmovies.presentation.theme.CustomMaterialTheme
import org.example.kmpsamples.presentation.fillMaxSizeModifier
import org.example.kmpsamples.presentation.transitions.ListItem
import org.example.kmpsamples.presentation.transitions.TransitionListItem

@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Preview
@Composable
private fun TransitionListItemPreview() {
    CustomMaterialTheme {
        Surface(fillMaxSizeModifier.safeDrawingPadding()) {
            TransitionListItem(
                Modifier.requiredSize(200.dp, 300.dp),
                ListItem(1, "Pineapple", Color.Red)
            )
        }
    }
}