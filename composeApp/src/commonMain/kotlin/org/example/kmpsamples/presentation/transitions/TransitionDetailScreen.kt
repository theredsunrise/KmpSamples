package org.example.kmpsamples.presentation.transitions

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kmpsamples.composeapp.generated.resources.Res
import kmpsamples.composeapp.generated.resources.pineapple
import org.example.kmpsamples.presentation.fillMaxWidthModifier
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TransitionDetailScreen(
    modifier: Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentTransitionScope: AnimatedContentScope,
    itemId: Int
) {
    with(sharedTransitionScope) {
        Column(modifier.background(Color.Blue)) {
            Image(
                vectorResource(Res.drawable.pineapple),
                itemId.toString(),
                Modifier.sharedBounds(
                    rememberSharedContentState(itemId),
                    animatedContentTransitionScope
                ).fillMaxSize()
                    .weight(0.5f).background(Color.Green)
            )
            Box(fillMaxWidthModifier.weight(0.5f).background(Color.Red))
        }
    }
}